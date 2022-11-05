package com.nyfaria.numismaticoverhaul.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ExampleClientConfig {

    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final ExampleClientConfig CLIENT;

    static {
        Pair<ExampleClientConfig, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(ExampleClientConfig::new);
        CLIENT_SPEC = pair.getRight();
        CLIENT = pair.getLeft();
    }

    public ForgeConfigSpec.BooleanValue example;
    public ForgeConfigSpec.IntValue pursePositionX;
    public ForgeConfigSpec.IntValue pursePositionY;

    public ExampleClientConfig(ForgeConfigSpec.Builder builder) {
        builder.push("client");
        example = builder.define("example", true);
        pursePositionX = builder.defineInRange("pursePositionX", 129, 0, 1000);
        pursePositionY = builder.defineInRange("pursePositionY", 20, 0, 2000);
        builder.pop();
        builder.build();
    }
}

