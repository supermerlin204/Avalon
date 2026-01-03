package com.merlin204.avalon.api.register;

public class AvalonAPI {
    /**
     * 供附属模组调用，注册其带有 @AvalonAutoRegister 注解的实体类
     * @param entityClass 附属模组的实体注册类（例如 ExampleEntities.class）
     * 添加类似
     * static {AvalonAPI.registerEntitiesFrom(ExampleEntities.class);}
     * 的一行即可
     */
    public static void registerEntitiesFrom(Class<?> entityClass) {
        AvalonEntityRegistryManager.scanAndRegisterEntities(entityClass);
    }


}
