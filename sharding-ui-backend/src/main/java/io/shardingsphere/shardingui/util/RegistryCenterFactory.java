/*
 * Copyright 2016-2018 shardingsphere.io.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package io.shardingsphere.shardingui.util;

import io.shardingsphere.orchestration.reg.api.RegistryCenter;
import io.shardingsphere.orchestration.reg.api.RegistryCenterConfiguration;
import io.shardingsphere.orchestration.reg.etcd.EtcdRegistryCenter;
import io.shardingsphere.orchestration.reg.zookeeper.curator.CuratorZookeeperRegistryCenter;
import io.shardingsphere.shardingui.common.constant.RegistryCenterType;
import io.shardingsphere.shardingui.common.domain.RegistryCenterConfig;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry center factory.
 *
 * @author chenqingyang
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RegistryCenterFactory {
    
    private static final ConcurrentHashMap<String, RegistryCenter> REGISTRY_CENTER_MAP = new ConcurrentHashMap<>();
    
    /**
     * Create registry center instance.
     *
     * @param config registry center config
     * @return registry center
     */
    public static RegistryCenter createRegistryCenter(final RegistryCenterConfig config) {
        RegistryCenter result = REGISTRY_CENTER_MAP.get(config.getName());
        if (null != result) {
            return result;
        }
        RegistryCenterType registryCenterType = RegistryCenterType.nameOf(config.getRegistryCenterType());
        switch (registryCenterType) {
            case ZOOKEEPER:
                result = new CuratorZookeeperRegistryCenter();
                break;
            case ETCD:
                result = new EtcdRegistryCenter();
                break;
            default:
                throw new UnsupportedOperationException(config.getName());
        }
        result.init(convert(config));
        REGISTRY_CENTER_MAP.put(config.getName(), result);
        return result;
    }
    
    private static RegistryCenterConfiguration convert(final RegistryCenterConfig config) {
        RegistryCenterConfiguration result = new RegistryCenterConfiguration();
        result.setServerLists(config.getServerLists());
        result.setNamespace(config.getNamespace());
        result.setDigest(config.getDigest());
        return result;
    }
    
}
