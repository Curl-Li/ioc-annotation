package com.annotation.test;

import com.annotation.util.PropertiesHandler;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedHashMap;
import java.util.Map;

public class PropertiesHandlerImpl implements PropertiesHandler {

    private final static DumperOptions OPTIONS = new DumperOptions();

    private static LinkedHashMap<String,String> data;

    private static String filename;

    static {
        //将默认读取的方式设置为块状读取
        OPTIONS.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
    }

    /**
     * 初始化配置类
     * @throws FileNotFoundException 文件不存在
     */
    public PropertiesHandlerImpl() throws FileNotFoundException {

        if(null==filename||"".equals(filename)){
            throw new FileNotFoundException("FileName Of Config Is NULL");
        }
        File source=new File(filename);
        if(!source.exists()){
            throw new FileNotFoundException("Config File Not Found");
        }
        Yaml yaml = new Yaml(OPTIONS);
        data=readMap(yaml.load(new FileReader(source)),"");

    }

    /**
     * 设置配置文件地址
     * @param filename 文件路径
     */
    public static void setFilename(String filename) {
        PropertiesHandlerImpl.filename = filename;
    }


    /**
     * 递归查询所有键值
     * @param data Map
     * @param prefix 前缀
     * @return 当前层及以下层所有键值对
     */
    private static LinkedHashMap<String,String> readMap(LinkedHashMap<String,Object> data,String prefix){

        LinkedHashMap<String,String> result=new LinkedHashMap<>();

        if(null!=prefix&&!"".equals(prefix)){
            prefix+=".";
        }

        for (Map.Entry<String, Object> entry :
                data.entrySet()) {

            String key=entry.getKey();

            Object value=entry.getValue();

            if(value instanceof LinkedHashMap){
                result.putAll(readMap((LinkedHashMap<String,Object>) value,prefix+key));
            }else {
                result.put(prefix+key,value.toString());
            }
        }

        return result;

    }


    /**
     * 取键对应的值
     * @param key 键
     * @return 值
     */
    @Override
    public String get(String key) {
        return data.get(key);
    }

}
