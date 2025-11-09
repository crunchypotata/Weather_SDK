package org.iakimova.wsdk;

/**
 * Mode of SDK behavior.
 * ON_DEMAND - update data only on request.
 * POLLING - continuously refresh data in background.
 */
public enum Mode {
    ON_DEMAND,
    POLLING
}
