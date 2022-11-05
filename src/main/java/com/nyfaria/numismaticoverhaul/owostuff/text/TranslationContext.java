package com.nyfaria.numismaticoverhaul.owostuff.text;

import net.minecraft.network.chat.contents.TranslatableContents;

import java.util.ArrayList;
import java.util.List;

public class TranslationContext {
    private static final ThreadLocal<List<TranslatableContents>> translationStack = ThreadLocal.withInitial(ArrayList::new);

    public static boolean pushContent(TranslatableContents content) {
        var stack = translationStack.get();

        for (int i = 0; i < stack.size(); i++) {
            if (stack.get(i) == content)
                return false;
        }

        stack.add(content);

        return true;
    }

    public static void popContent() {
        var stack = translationStack.get();

        stack.remove(stack.size() - 1);
    }

    public static TranslatableContents getCurrent() {
        var stack = translationStack.get();

        if (stack.size() <= 0)
            return null;
        else
            return stack.get(stack.size() - 1);
    }
}
