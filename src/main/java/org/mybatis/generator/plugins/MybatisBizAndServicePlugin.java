package org.mybatis.generator.plugins;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.util.StringUtility;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * MyBatis BizAndService 生成插件
 * Created by guoZhiHao on 2017/5/23.
 */
public class MybatisBizAndServicePlugin extends MybatisServicePlugin{

    private FullyQualifiedJavaType componentAnnotation;
    private FullyQualifiedJavaType bizType;

    private String bizPackage;

    @Override
    public boolean validate(List<String> warnings) {
        boolean validate = super.validate(warnings);
        this.bizPackage = properties.getProperty("bizPackage");
        if (super.enableSpringAnnotation) {
            componentAnnotation = new FullyQualifiedJavaType("org.springframework.stereotype.Component");
        }
        return validate;
    }

    /**
     * 生成文件的主方法
     *
     * @param introspectedTable
     * @return
     */
    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        List<GeneratedJavaFile> files = new ArrayList<GeneratedJavaFile>();
        String table = introspectedTable.getBaseRecordType();
        String tableName = table.replaceAll(pojoPackage + ".", "");
        //接口文件名称
        serviceType = new FullyQualifiedJavaType(servicePackage + "." + tableName + "Service");

        // mybatis
        daoType = new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType());

        // 实现类文件名称
        serviceImplType = new FullyQualifiedJavaType(serviceImplPackage + "." + tableName + "ServiceImpl");

        // biz文件名称
        bizType = new FullyQualifiedJavaType(bizPackage + "." + tableName + "Biz");

        //实体类全路径
        pojoType = new FullyQualifiedJavaType(pojoPackage + "." + tableName);

        Interface serviceInterface = new Interface(serviceType);
        TopLevelClass topLevelServiceClass = new TopLevelClass(serviceImplType);
        TopLevelClass topLevelBizClass = new TopLevelClass(bizType);
        //service接口导入包
        serviceInterface.addImportedType(pojoType);
        // 导入必要的类
        addImport(topLevelServiceClass);
        addImport(topLevelBizClass);
        // 接口
        addService(serviceInterface, introspectedTable, getBizShort(), files);
        // 实现类
        addServiceImpl(topLevelServiceClass, introspectedTable, getBizShort(), files,bizType);

        //Biz类
        addBiz(topLevelBizClass, introspectedTable, getDaoShort(), files,daoType);

        return files;
    }


    /**
     * 导入需要的类
     */
    protected void addImport(TopLevelClass topLevelClass) {
        topLevelClass.addImportedType(pojoType);
        topLevelClass.addImportedType(daoType);
        topLevelClass.addImportedType(serviceType);
        if (enableSpringAnnotation) {
            topLevelClass.addImportedType(serviceAnnotation);
            topLevelClass.addImportedType(autowiredAnnotation);
        }
    }

    /**
     * 添加实现类
     *
     * @param introspectedTable
     * @param shortName
     * @param files
     */
    protected void addBiz(TopLevelClass topLevelClass, IntrospectedTable introspectedTable, String shortName, List<GeneratedJavaFile> files,FullyQualifiedJavaType type) {

        //添加注释
        topLevelClass.addJavaDocLine(super.getJavadocTag(introspectedTable.getFullyQualifiedTable().getRemarks()));

        topLevelClass.setVisibility(JavaVisibility.PUBLIC);

        if (enableSpringAnnotation) {
            topLevelClass.addImportedType(componentAnnotation);
            topLevelClass.addAnnotation("@Component");
        }
        // 添加引用dao
        addField(topLevelClass, type);
        // 添加方法
        topLevelClass.addMethod(selectByPrimaryKey(introspectedTable, shortName));

        /**
         * type 的意义 pojo 1 ;key 2 ;example 3 ;pojo+example 4
         */
        if (enableDeleteByPrimaryKey) {
            topLevelClass.addMethod(addOtherMethod("deleteByPrimaryKey", introspectedTable, shortName, 2));
        }
        if (enableUpdateByPrimaryKeySelective) {
            topLevelClass.addMethod(addOtherMethod("updateByPrimaryKeySelective", introspectedTable, shortName, 1));

        }
        if (enableUpdateByPrimaryKey) {
            topLevelClass.addMethod(addOtherMethod("updateByPrimaryKey", introspectedTable, shortName, 1));
        }
        if (enableDeleteByExample) {
            topLevelClass.addMethod(addOtherMethod("deleteByExample", introspectedTable, shortName, 3));
        }
        if (enableUpdateByExampleSelective) {
            topLevelClass.addMethod(addOtherMethod("updateByExampleSelective", introspectedTable, shortName, 4));
        }
        if (enableUpdateByExample) {
            topLevelClass.addMethod(addOtherMethod("updateBy" +
                    "Example", introspectedTable, shortName, 4));
        }
        if (enableInsert) {
            topLevelClass.addMethod(getInsertMethod("insert",shortName));
        }
        if (enableInsertSelective) {
            topLevelClass.addMethod(getInsertMethod("insertSelective",shortName));
        }
        // 生成文件
        GeneratedJavaFile file = new GeneratedJavaFile(topLevelClass, project,context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING), context.getJavaFormatter());
        files.add(file);
    }

    /**
     * 获得:xxxBiz.
     *
     * @return
     */
    private String getBizShort() {
        return this.toLowerCase(bizType.getShortName()) + ".";
    }
}
