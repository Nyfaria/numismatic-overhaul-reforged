package com.nyfaria.numismaticoverhaul.owostuff.ui.parsing;

import com.nyfaria.numismaticoverhaul.owostuff.ui.component.ButtonComponent;
import com.nyfaria.numismaticoverhaul.owostuff.ui.component.Components;
import com.nyfaria.numismaticoverhaul.owostuff.ui.component.DiscreteSliderComponent;
import com.nyfaria.numismaticoverhaul.owostuff.ui.component.EntityComponent;
import com.nyfaria.numismaticoverhaul.owostuff.ui.component.SpriteComponent;
import com.nyfaria.numismaticoverhaul.owostuff.ui.component.TextureComponent;
import com.nyfaria.numismaticoverhaul.owostuff.ui.component.TexturedButtonComponent;
import com.nyfaria.numismaticoverhaul.owostuff.ui.container.CollapsibleContainer;
import com.nyfaria.numismaticoverhaul.owostuff.ui.container.Containers;
import com.nyfaria.numismaticoverhaul.owostuff.ui.container.FlowLayout;
import com.nyfaria.numismaticoverhaul.owostuff.ui.container.GridLayout;
import com.nyfaria.numismaticoverhaul.owostuff.ui.container.ScrollContainer;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.ModComponent;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.Sizing;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A utility class containing the component factory registry
 * as well as some utility functions to ease model parsing
 */
public class UIParsing {

    private static final Map<String, Function<Element, ModComponent>> COMPONENT_FACTORIES = new HashMap<>();

    /**
     * Register a factory used to create components from XML elements.
     * Most factories will only consider the tag name of the element,
     * but more context can be extracted from the passed element
     *
     * @param componentTagName The tag name of elements for which the
     *                         passed factory should be invoked
     * @param factory          The factory to register
     */
    public static void registerFactory(String componentTagName, Function<Element, ModComponent> factory) {
        if (COMPONENT_FACTORIES.containsKey(componentTagName)) {
            throw new IllegalStateException("A component factory with name " + componentTagName + " is already registered");
        }

        COMPONENT_FACTORIES.put(componentTagName, factory);
    }

    /**
     * Get the appropriate component factory for the given
     * XML element. An exception is thrown if none is registered
     *
     * @param element The element representing the component to be parsed
     * @return The matching factory
     * @throws UIModelParsingException If there is no registered factory
     *                                 capable of parsing the given element
     */
    public static Function<Element, ModComponent> getFactory(Element element) {
        var factory = COMPONENT_FACTORIES.get(element.getNodeName());
        if (factory == null) {
            throw new UIModelParsingException("Unknown component type: " + element.getNodeName());
        }

        return factory;
    }

    /**
     * Extract all children of the given element which match the expected type
     *
     * @param type The type of child nodes to extract
     * @param <T>  The class to cast the extracted nodes to
     * @return A list of all children of {@code element} which have a type of {@code type}
     */
    @SuppressWarnings("unchecked")
    public static <T extends Node> List<T> allChildrenOfType(Element element, short type) {
        var list = new ArrayList<T>();
        for (int i = 0; i < element.getChildNodes().getLength(); i++) {
            var child = element.getChildNodes().item(i);
            if (child.getNodeType() != type) continue;
            list.add((T) child);
        }
        return list;
    }

    /**
     * Extract all child elements of the given element into a map from tag
     * name to element. An exception is thrown if a tag name appears twice
     *
     * @return All element children of {@code element} mapped from
     * tag name to element
     * @throws UIModelParsingException If two or more children share the same tag name
     */
    public static Map<String, Element> childElements(Element element) {
        var children = element.getChildNodes();
        var map = new HashMap<String, Element>();

        for (int i = 0; i < children.getLength(); i++) {
            var child = children.item(i);
            if (child.getNodeType() != Node.ELEMENT_NODE) continue;

            if (map.containsKey(child.getNodeName())) {
                throw new UIModelParsingException("Duplicate child " + child.getNodeName() + " in element " + element.getNodeName());
            }

            map.put(child.getNodeName(), (Element) child);
        }

        return map;
    }

    /**
     * Tries to interpret the text content of the
     * given node as a signed integer
     *
     * @throws UIModelParsingException If the text content does not
     *                                 represent a valid signed integer
     */
    public static int parseSignedInt(Node node) {
        return parseInt(node, true);
    }

    /**
     * Tries to interpret the text content of the
     * given node as an unsigned integer
     *
     * @throws UIModelParsingException If the text content does not
     *                                 represent a valid unsigned integer
     */
    public static int parseUnsignedInt(Node node) {
        return parseInt(node, false);
    }

    /**
     * Tries to interpret the text content of the
     * given node as a floating-point number
     *
     * @throws UIModelParsingException If the text content does not
     *                                 represent a valid floating point number
     */
    public static float parseFloat(Node node) {
        var data = node.getTextContent().strip();
        if (data.matches("-?\\d+(\\.\\d+)?")) {
            return Float.parseFloat(data);
        } else {
            throw new UIModelParsingException("Invalid value '" + data + "', expected a floating point number");
        }
    }

    /**
     * Tries to interpret the text content of the
     * given node as a double-precision floating-point number
     *
     * @throws UIModelParsingException If the text content does not
     *                                 represent a valid floating point number
     */
    public static double parseDouble(Node node) {
        var data = node.getTextContent().strip();
        if (data.matches("-?\\d+(\\.\\d+)?")) {
            return Double.parseDouble(data);
        } else {
            throw new UIModelParsingException("Invalid value '" + data + "', expected a double-precision floating point number");
        }
    }

    /**
     * Interprets the text content of the
     * given node as a boolean - more specifically this
     * method returns {@code true} if and only if the text content
     * equals {@code true}, without respecting letter case
     */
    public static boolean parseBool(Node node) {
        return node.getTextContent().strip().equalsIgnoreCase("true");
    }

    /**
     * Tries to interpret the text content of the
     * given node as an identifier
     *
     * @throws UIModelParsingException If the text content does not
     *                                 represent a valid identifier
     */
    public static ResourceLocation parseIdentifier(Node node) {
        try {
            return new ResourceLocation(node.getTextContent().strip());
        } catch (ResourceLocationException exception) {
            throw new UIModelParsingException("Invalid identifier '" + node.getTextContent() + "'", exception);
        }
    }

    /**
     * Interprets the text content of the
     * given element as text. If the {@code translate}
     * attribute is set to {@code true}, the content is
     * interpreted as a translation key - otherwise it is
     * returned literally
     */
    public static net.minecraft.network.chat.Component parseText(Element element) {
        return element.getAttribute("translate").equalsIgnoreCase("true")
                ? net.minecraft.network.chat.Component.translatable(element.getTextContent())
                : net.minecraft.network.chat.Component.literal(element.getTextContent());
    }

    public static <E extends Enum<E>> Function<Element, E> parseEnum(Class<E> enumClass) {
        return element -> {
            var name = element.getTextContent().strip().toUpperCase(Locale.ROOT).replace('-', '_');
            for (var value : enumClass.getEnumConstants()) {
                if (Objects.equals(name, value.name())) return value;
            }

            throw new UIModelParsingException("No such constant " + name + " in enum " + enumClass.getSimpleName());
        };
    }

    /**
     * Parse the property indicated by {@code key} into an object of type {@code T}
     *
     * @param properties The map containing all available properties
     * @param key        The key of the property to parse
     * @param parser     The parsing function to use
     * @param <T>        The type of object to parse
     * @return An optional containing the parsed property, or an empty optional
     * if the requested property was not contained in the given map
     */
    public static <T, E extends Node> Optional<T> get(Map<String, E> properties, String key, Function<E, T> parser) {
        if (!properties.containsKey(key)) return Optional.empty();
        return Optional.of(parser.apply(properties.get(key)));
    }

    /**
     * Parse the property indicated by {@code key} into an object of type {@code T}
     * and apply the given function if it was present
     *
     * @param properties The map containing all available properties
     * @param key        The key of the property to parse
     * @param parser     The parsing function to use
     * @param consumer   The function to apply if the property was present
     *                   in the map and successfully parsed
     * @param <T>        The type of object to parse
     */
    public static <T, E extends Node> void apply(Map<String, E> properties, String key, Function<E, T> parser, Consumer<T> consumer) {
        if (!properties.containsKey(key)) return;
        consumer.accept(parser.apply(properties.get(key)));
    }

    /**
     * Verify that all the given attributes are present
     * on the given element and throw if one is missing
     *
     * @param element    The element to verify
     * @param attributes The attributes to verify
     */
    public static void expectAttributes(Element element, String... attributes) {
        for (var attr : attributes) {
            if (!element.hasAttribute(attr)) {
                throw new UIModelParsingException("Element '" + element.getNodeName() + "' is missing attribute '" + attr + "'");
            }
        }
    }

    /**
     * Verify that all the given elements are present
     * as children of the given element and throw if one is missing
     *
     * @param element  The element to verify
     * @param children The children of that element
     * @param expected The expected child elements
     */
    public static void expectChildren(Element element, Map<String, Element> children, String... expected) {
        for (var childName : expected) {
            if (!children.containsKey(childName)) {
                throw new UIModelParsingException("Element '" + element.getNodeName() + "' is missing element '" + childName + "'");
            }
        }
    }

    protected static int parseInt(Node node, boolean allowNegative) {
        var data = node.getTextContent().strip();
        if (data.matches((allowNegative ? "-?" : "") + "\\d+")) {
            return Integer.parseInt(data);
        } else {
            throw new UIModelParsingException("Invalid value '" + data + "', expected " + (allowNegative ? "" : "positive") + " integer");
        }
    }

    static {
        // Layout
        registerFactory("flow-layout", FlowLayout::parse);
        registerFactory("grid-layout", GridLayout::parse);

        // Container
        registerFactory("scroll", ScrollContainer::parse);
        registerFactory("collapsible", CollapsibleContainer::parse);
        registerFactory("draggable", element -> Containers.draggable(Sizing.content(), Sizing.content(), null));

        // Textures
        registerFactory("sprite", SpriteComponent::parse);
        registerFactory("texture", TextureComponent::parse);

        // Game Objects
        registerFactory("entity", EntityComponent::parse);
        registerFactory("item", element -> Components.item(ItemStack.EMPTY));

        // Widgets
        registerFactory("label", element -> Components.label(net.minecraft.network.chat.Component.empty()));
        registerFactory("box", element -> Components.box(Sizing.content(), Sizing.content()));
        registerFactory("button", element -> Components.button(net.minecraft.network.chat.Component.empty(), (ButtonComponent button) -> {}));
        registerFactory("textured-button", element->(ModComponent)TexturedButtonComponent.parse(element));
        registerFactory("checkbox", element -> Components.checkbox(net.minecraft.network.chat.Component.empty()));
        registerFactory("text-box", element -> (ModComponent) Components.textBox(Sizing.content()));
        registerFactory("slider", element -> (ModComponent)Components.slider(Sizing.content()));
        registerFactory("discrete-slider", element ->(ModComponent)DiscreteSliderComponent.parse(element));
        registerFactory("dropdown", element -> Components.dropdown(Sizing.content()));
    }

}
