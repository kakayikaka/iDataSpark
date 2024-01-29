package io.maling.io;

import java.io.InputStream;

/**
 * iDataSpark 配置文件资源加载类
 * @author maling
 */
public class Resources {

    /**
     * 根据配置文件路径，将资源加载成字节输入楼，并返回
     * @param path 资源路径
     * @return 字节输入流
     */
    public static InputStream getResourceAsStream(String path) {
        // 利用类加载器，直接加载即可
        return Resources.class.getClassLoader().getResourceAsStream(path);
    }
}
