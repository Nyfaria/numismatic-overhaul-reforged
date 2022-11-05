package com.nyfaria.numismaticoverhaul.owostuff.ui.inject;

import com.nyfaria.numismaticoverhaul.owostuff.ui.core.ModComponent;

/**
 * A marker interface for components which consume
 * text input when focused - this is used to prevent handled
 * screens from closing when said component is focused and the
 * inventory key is pressed
 */
public interface GreedyInputComponent extends ModComponent {}