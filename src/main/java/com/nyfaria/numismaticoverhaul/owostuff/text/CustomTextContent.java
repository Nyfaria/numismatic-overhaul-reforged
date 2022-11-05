package com.nyfaria.numismaticoverhaul.owostuff.text;

import net.minecraft.network.chat.ComponentContents;

public interface CustomTextContent extends ComponentContents {
    CustomTextContentSerializer<?> serializer();
}
