package org.wens.mybatis.gererator.plugin;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.config.JavaClientGeneratorConfiguration;

import java.io.*;
import java.nio.file.Paths;
import java.util.List;

public class XMapperPlugin extends PluginAdapter {
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        JavaClientGeneratorConfiguration javaClientGeneratorConfiguration = introspectedTable.getContext().getJavaClientGeneratorConfiguration();

        if(javaClientGeneratorConfiguration == null ){
            return true;
        }

        int i = introspectedTable.getMyBatis3JavaMapperType().lastIndexOf(".");
        String packageN = introspectedTable.getMyBatis3JavaMapperType().substring(0,i);
        String supperMapper = introspectedTable.getMyBatis3JavaMapperType().substring(i+1);
        File xMapperFile = new File( Paths.get(javaClientGeneratorConfiguration.getTargetProject(), javaClientGeneratorConfiguration.getTargetPackage().split("\\.")).toString(), String.format("X%s.java",supperMapper));

        if(xMapperFile.exists()){
            return true ;
        }

        if(!xMapperFile.getParentFile().exists()){
            xMapperFile.getParentFile().mkdirs();
        }



        Interface xMapper = new Interface(packageN + ".X" + supperMapper);
        xMapper.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Mapper"));

        if(introspectedTable.getTargetRuntime() == IntrospectedTable.TargetRuntime.MYBATIS3_DSQL){
            xMapper.addStaticImport(String.format("%s.*",introspectedTable.getMyBatisDynamicSqlSupportType()));
            xMapper.addStaticImport("org.mybatis.dynamic.sql.SqlBuilder.*");
        }

        xMapper.addSuperInterface(new FullyQualifiedJavaType(supperMapper));
        xMapper.setVisibility(JavaVisibility.PUBLIC);
        xMapper.addAnnotation("@Mapper");

        try( OutputStreamWriter out  = new OutputStreamWriter(new FileOutputStream(xMapperFile),"utf-8")){
            out.write(xMapper.getFormattedContent());
        }catch (IOException e){
            throw new RuntimeException(e);
        }

        return true;
    }
}
