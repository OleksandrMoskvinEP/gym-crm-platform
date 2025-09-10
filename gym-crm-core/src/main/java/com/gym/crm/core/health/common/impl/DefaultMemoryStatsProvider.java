package com.gym.crm.core.health.common.impl;

import com.gym.crm.core.health.common.MemoryStatsProvider;
import org.springframework.stereotype.Component;

@Component
public class DefaultMemoryStatsProvider implements MemoryStatsProvider {
    @Override
    public long getFreeMemory() {
        return Runtime.getRuntime().freeMemory();
    }
}
