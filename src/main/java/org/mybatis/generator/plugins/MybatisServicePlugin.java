package org.mybatis.generator.plugins;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaElement;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.util.StringUtility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * MyBatis Service 生成插件
 * Created by guoZhiHao on 2017/5/22.
 */
public class MybatisServicePlugin extends PluginAdapter {

    // private FullyQualifiedJavaType slf4jLogger;
    // private FullyQualifiedJavaType slf4jLoggerFactory;
    protected FullyQualifiedJavaType serviceType;
    protected FullyQualifiedJavaType daoType;
    protected FullyQualifiedJavaType serviceImplType;
    protected FullyQualifiedJavaType pojoType;
    protected FullyQualifiedJavaType pojoCriteriaType;
    protected FullyQualifiedJavaType autowiredAnnotation;
    protected FullyQualifiedJavaType serviceAnnotation;
    protected FullyQualifiedJavaType returnType;
    protected String servicePackage;
    protected String serviceImplPackage;
    protected String project;
    protected String pojoPackage;

    private List<Method> methods;
    /**
     * 是否添加注解
     */
    protected boolean enableSpringAnnotation = true;
    protected boolean enableInsert = true;
    protected boolean enableInsertSelective = true;
    protected boolean enableDeleteByPrimaryKey = true;
    protected boolean enableUpdateByPrimaryKey = true;
    protected boolean enableUpdateByPrimaryKeySelective = true;
    protected boolean enableDeleteByExample = false;
    protected boolean enableUpdateByExample = false;
    protected boolean enableUpdateByExampleSelective = false;


    /**
     * Date 格式化
     */
    protected SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public MybatisServicePlugin() {
        super();
        this.methods = new ArrayList<Method>();

    }

    /**
     * 初始化参数
     *
     * @param warnings add strings to this list to specify warnings. For example, if
     *                 the plugin is invalid, you should specify why. Warnings are
     *                 reported to users after the completion of the run.
     * @return
     */
    @Override
    public boolean validate(List<String> warnings) {
        //从context中获得是否开启spring注解
        String property = context.getProperty(PropertyRegistry.CONTEXT_ENABLE_SPRING_ANNOTATION);
        enableSpringAnnotation = StringUtility.isTrue(property);

        String enableInsert = properties.getProperty("enableInsert");

        String enableInsertSelective = properties.getProperty("enableInsertSelective");

        String enableUpdateByPrimaryKey = properties.getProperty("enableUpdateByPrimaryKey");

        String enableUpdateByPrimaryKeySelective = properties.getProperty("enableUpdateByPrimaryKeySelective");

        String enableDeleteByPrimaryKey = properties.getProperty("enableDeleteByPrimaryKey");

        String enableUpdateByExampleSelective = properties.getProperty("enableUpdateByExampleSelective");

        String enableUpdateByExample = properties.getProperty("enableUpdateByExample");

        String enableDeleteByExample = properties.getProperty("enableDeleteByExample");

        if (StringUtility.stringHasValue(enableInsert))
            this.enableInsert = StringUtility.isTrue(enableInsert);
        if (StringUtility.stringHasValue(enableUpdateByExampleSelective))
            this.enableUpdateByExampleSelective = StringUtility.isTrue(enableUpdateByExampleSelective);
        if (StringUtility.stringHasValue(enableInsertSelective))
            this.enableInsertSelective = StringUtility.isTrue(enableInsertSelective);
        if (StringUtility.stringHasValue(enableUpdateByPrimaryKey))
            this.enableUpdateByPrimaryKey = StringUtility.isTrue(enableUpdateByPrimaryKey);
        if (StringUtility.stringHasValue(enableDeleteByPrimaryKey))
            this.enableDeleteByPrimaryKey = StringUtility.isTrue(enableDeleteByPrimaryKey);
        if (StringUtility.stringHasValue(enableDeleteByExample))
            this.enableDeleteByExample = StringUtility.isTrue(enableDeleteByExample);
        if (StringUtility.stringHasValue(enableUpdateByPrimaryKeySelective))
            this.enableUpdateByPrimaryKeySelective = StringUtility.isTrue(enableUpdateByPrimaryKeySelective);
        if (StringUtility.stringHasValue(enableUpdateByExample))
            this.enableUpdateByExample = StringUtility.isTrue(enableUpdateByExample);

        /**
         * service包名
         */
        servicePackage = properties.getProperty("targetPackage");
        /**
         * serviceImplement 包名
         */
        serviceImplPackage = properties.getProperty("implementationPackage");
        project = properties.getProperty("targetProject");

        pojoPackage = context.getJavaModelGeneratorConfiguration().getTargetPackage();

        /**
         * 如果启用注解，则添加@AutoWired和@Service注解
         */
        if (enableSpringAnnotation) {
            autowiredAnnotation = new FullyQualifiedJavaType("org.springframework.beans.factory.annotation.Autowired");
            serviceAnnotation = new FullyQualifiedJavaType("org.springframework.stereotype.Service");
        }
        return true;
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
        String tableName = table.replaceAll(this.pojoPackage + ".", "");
        //接口文件名称
        serviceType = new FullyQualifiedJavaType(servicePackage + "." + tableName + "Service");

        // mybatis
        daoType = new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType());

        // 实现类文件名称
        serviceImplType = new FullyQualifiedJavaType(serviceImplPackage + "." + tableName + "ServiceImpl");

        //实体类全路径
        pojoType = new FullyQualifiedJavaType(pojoPackage + "." + tableName);

        pojoCriteriaType = new FullyQualifiedJavaType(pojoPackage + "." + "Criteria");
        Interface serviceInterface = new Interface(serviceType);
        TopLevelClass topLevelClass = new TopLevelClass(serviceImplType);
        // 导入必要的类
        addImport(serviceInterface, topLevelClass);

        // 接口
        addService(serviceInterface, introspectedTable, getDaoShort(), files);
        // 实现类
        addServiceImpl(topLevelClass, introspectedTable, getDaoShort(), files,daoType);
        // addLogger(topLevelClass);

        return files;
    }

    /**
     * 添加接口方法
     *
     * @param shortName
     * @param files
     */
    protected void addService(Interface interface1, IntrospectedTable introspectedTable, String shortName, List<GeneratedJavaFile> files) {
        //添加注释
        interface1.addJavaDocLine(this.getJavadocTag(introspectedTable.getFullyQualifiedTable().getRemarks()));

        interface1.setVisibility(JavaVisibility.PUBLIC);

        // 此处可自定义方法

        Method method = selectByPrimaryKey(introspectedTable, shortName);
        method.removeAllBodyLines();
        interface1.addMethod(method);

        if (enableInsert) {
            method = getInsertMethod("insert",shortName);
            method.removeAllBodyLines();
            interface1.addMethod(method);
        }
        if (enableInsertSelective) {
            method = getInsertMethod("insertSelective",shortName);
            method.removeAllBodyLines();
            interface1.addMethod(method);
        }
        if (enableUpdateByPrimaryKey) {
            method = addOtherMethod("updateByPrimaryKey", introspectedTable, shortName, 1);
            method.removeAllBodyLines();
            interface1.addMethod(method);
        }

        if (enableUpdateByPrimaryKeySelective) {
            method = addOtherMethod("updateByPrimaryKeySelective", introspectedTable, shortName, 1);
            method.removeAllBodyLines();
            interface1.addMethod(method);
        }
        if (enableDeleteByPrimaryKey) {
            method = addOtherMethod("deleteByPrimaryKey", introspectedTable, shortName, 2);
            method.removeAllBodyLines();
            interface1.addMethod(method);
        }

        if (enableDeleteByExample) {
            method = addOtherMethod("deleteByExample", introspectedTable, shortName, 3);
            method.removeAllBodyLines();
            interface1.addMethod(method);
        }
        if (enableUpdateByExampleSelective) {
            method = addOtherMethod("updateByExampleSelective", introspectedTable, shortName, 4);
            method.removeAllBodyLines();
            interface1.addMethod(method);
        }
        if (enableUpdateByExample) {
            method = addOtherMethod("updateByExample", introspectedTable, shortName, 4);
            method.removeAllBodyLines();
            interface1.addMethod(method);
        }

        GeneratedJavaFile file = new GeneratedJavaFile(interface1, project,context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING) ,context.getJavaFormatter());
        files.add(file);
    }

    /**
     * 添加实现类
     *
     * @param introspectedTable
     * @param shortName
     * @param files
     */
    protected void addServiceImpl(TopLevelClass topLevelClass, IntrospectedTable introspectedTable, String shortName, List<GeneratedJavaFile> files,FullyQualifiedJavaType type) {

        //添加注释
        topLevelClass.addJavaDocLine(this.getJavadocTag(introspectedTable.getFullyQualifiedTable().getRemarks()));

        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        // 设置实现的接口
        topLevelClass.addSuperInterface(serviceType);

        if (enableSpringAnnotation) {
            topLevelClass.addImportedType(serviceAnnotation);
            topLevelClass.addAnnotation("@Service");
        }
        // 添加引用dao/Biz
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
     * 添加注入字段
     *
     * @param topLevelClass
     */
    protected void addField(TopLevelClass topLevelClass, FullyQualifiedJavaType type) {
        // 添加 dao/biz
        Field field = new Field();
        field.setName(this.toLowerCase(type.getShortName())); // 设置变量名,首字母转成小写
        topLevelClass.addImportedType(type);
        field.setType(type); // 类型
        field.setVisibility(JavaVisibility.PRIVATE);
        if (enableSpringAnnotation) {
            field.addAnnotation("@Autowired");
        }
        topLevelClass.addField(field);
    }

    /**
     * selectByPrimaryKey方法
     * @param introspectedTable
     * @param shortName
     * @return
     */
    protected Method selectByPrimaryKey(IntrospectedTable introspectedTable, String shortName) {
        Method method = new Method();
        method.setName("selectByPrimaryKey");
        method.setReturnType(pojoType);
        if (introspectedTable.getRules().generatePrimaryKeyClass()) {
            FullyQualifiedJavaType type = new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType());
            method.addParameter(new Parameter(type, "key"));
        } else {
            for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
                FullyQualifiedJavaType type = introspectedColumn.getFullyQualifiedJavaType();
                method.addParameter(new Parameter(type, introspectedColumn.getJavaProperty()));
            }
        }
        method.setVisibility(JavaVisibility.PUBLIC);
        StringBuilder sb = new StringBuilder();
        sb.append("return this.");
        sb.append(shortName);
        sb.append("selectByPrimaryKey");
        sb.append("(");
        for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
            sb.append(introspectedColumn.getJavaProperty());
            sb.append(",");
        }
        sb.setLength(sb.length() - 1);
        sb.append(");");
        method.addBodyLine(sb.toString());
        return method;
    }


    /**
     * 添加方法
     * @param methodName
     * @param introspectedTable
     * @param shortName
     * @param type
     * @return
     */
    protected Method addOtherMethod(String methodName, IntrospectedTable introspectedTable, String shortName, int type) {
        Method method = new Method();
        method.setName(methodName);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        String params = addParams(introspectedTable, method, type);
        method.setVisibility(JavaVisibility.PUBLIC);
        StringBuilder sb = new StringBuilder();
        // method.addBodyLine("try {");
        sb.append("return this.");
        sb.append(shortName);
        if (introspectedTable.hasBLOBColumns()
                && (!"updateByPrimaryKeySelective".equals(methodName) && !"deleteByPrimaryKey".equals(methodName)
                && !"deleteByExample".equals(methodName) && !"updateByExampleSelective".equals(methodName))) {
            sb.append(methodName + "WithoutBLOBs");
        } else {
            sb.append(methodName);
        }
        sb.append("(");
        sb.append(params);
        sb.append(");");
        method.addBodyLine(sb.toString());
        return method;
    }

    /**
     * 添加插入方法
     */
    protected Method getInsertMethod(String methodName,String shortName) {
        Method method = new Method();
        method.setName(methodName);
        method.setReturnType(returnType);
        method.addParameter(new Parameter(pojoType, "record"));
        method.setVisibility(JavaVisibility.PUBLIC);
        StringBuilder sb = new StringBuilder();
        if (returnType == null) {
            sb.append("this.");
        } else {
            sb.append("return this.");
        }
        sb.append(shortName);
        sb.append(methodName);
        sb.append("(");
        sb.append("record");
        sb.append(");");
        method.addBodyLine(sb.toString());
        return method;
    }

    /**
     * type 的意义 pojo 1 key 2 example 3 pojo+example 4
     */
    protected String addParams(IntrospectedTable introspectedTable, Method method, int type1) {
        switch (type1) {
            case 1:
                method.addParameter(new Parameter(pojoType, "record"));
                return "record";
            case 2:
                if (introspectedTable.getRules().generatePrimaryKeyClass()) {
                    FullyQualifiedJavaType type = new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType());
                    method.addParameter(new Parameter(type, "key"));
                } else {
                    for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
                        FullyQualifiedJavaType type = introspectedColumn.getFullyQualifiedJavaType();
                        method.addParameter(new Parameter(type, introspectedColumn.getJavaProperty()));
                    }
                }
                StringBuilder sb = new StringBuilder();
                for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
                    sb.append(introspectedColumn.getJavaProperty());
                    sb.append(",");
                }
                sb.setLength(sb.length() - 1);
                return sb.toString();
            case 3:
                method.addParameter(new Parameter(pojoCriteriaType, "example"));
                return "example";
            case 4:

                method.addParameter(0, new Parameter(pojoType, "record"));
                method.addParameter(1, new Parameter(pojoCriteriaType, "example"));
                return "record, example";
            default:
                break;
        }
        return null;
    }

    protected void addComment(JavaElement field, String comment) {
        StringBuilder sb = new StringBuilder();
        field.addJavaDocLine("/**");
        sb.append(" * ");
        comment = comment.replaceAll("\n", "<br>\n\t * ");
        sb.append(comment);
        field.addJavaDocLine(sb.toString());
        field.addJavaDocLine(" */");
    }

    /**
     * 添加字段
     *
     * @param topLevelClass
     */
    protected void addField(TopLevelClass topLevelClass) {
        // 添加 success
        Field field = new Field();
        field.setName("success"); // 设置变量名
        field.setType(FullyQualifiedJavaType.getBooleanPrimitiveInstance()); // 类型
        field.setVisibility(JavaVisibility.PRIVATE);
        addComment(field, "执行结果");
        topLevelClass.addField(field);
        // 设置结果
        field = new Field();
        field.setName("message"); // 设置变量名
        field.setType(FullyQualifiedJavaType.getStringInstance()); // 类型
        field.setVisibility(JavaVisibility.PRIVATE);
        addComment(field, "消息结果");
        topLevelClass.addField(field);
    }

    /**
     * 添加方法
     */
    protected void addMethod(TopLevelClass topLevelClass) {
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName("setSuccess");
        method.addParameter(new Parameter(FullyQualifiedJavaType.getBooleanPrimitiveInstance(), "success"));
        method.addBodyLine("this.success = success;");
        topLevelClass.addMethod(method);

        method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getBooleanPrimitiveInstance());
        method.setName("isSuccess");
        method.addBodyLine("return success;");
        topLevelClass.addMethod(method);

        method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName("setMessage");
        method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "message"));
        method.addBodyLine("this.message = message;");
        topLevelClass.addMethod(method);

        method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getStringInstance());
        method.setName("getMessage");
        method.addBodyLine("return message;");
        topLevelClass.addMethod(method);
    }

    /**
     * 添加方法返回值
     */
    protected void addMethod(TopLevelClass topLevelClass, String shortName) {
        for (Method method : methods) {
            method.removeAllBodyLines();
            method.removeAnnotation();
            StringBuilder sb = new StringBuilder();
            sb.append("return this.");
            sb.append(shortName);
            sb.append(method.getName());
            sb.append("(");
            List<Parameter> list = method.getParameters();
            for (int j = 0; j < list.size(); j++) {
                sb.append(list.get(j).getName());
                sb.append(",");
            }
            sb.setLength(sb.length() - 1);
            sb.append(");");
            method.addBodyLine(sb.toString());
            topLevelClass.addMethod(method);
        }
        methods.clear();
    }

    /**
     * 导入需要的类
     */
    protected void addImport(Interface interfaces, TopLevelClass topLevelClass) {
        interfaces.addImportedType(pojoType);
        topLevelClass.addImportedType(pojoType);
        topLevelClass.addImportedType(daoType);
        topLevelClass.addImportedType(serviceType);
        if (enableSpringAnnotation) {
            topLevelClass.addImportedType(serviceAnnotation);
            topLevelClass.addImportedType(autowiredAnnotation);
        }
    }

    /**
     * 获得:xxxDao.
     *
     * @return
     */
    protected String getDaoShort() {
        return this.toLowerCase(daoType.getShortName()) + ".";
    }


    @Override
    public boolean clientInsertMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        returnType = method.getReturnType();
        return true;
    }

    /**
     * BaseUsers to baseUsers
     *
     * @param tableName
     * @return
     */
    protected String toLowerCase(String tableName) {
        StringBuilder sb = new StringBuilder(tableName);
        sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
        return sb.toString();
    }

    /**
     * 获得注释字符串
     *
     * @param remarks
     * @return
     */
    protected String getJavadocTag(String remarks) {
        StringBuilder sb = new StringBuilder();
        sb.append("/**");
        sb.append("\n * ");
        sb.append(remarks);
        sb.append("相关方法");
        sb.append("\n * Author:");
        sb.append(context.getCommentGeneratorConfiguration().getProperty(PropertyRegistry.COMMENT_GENERATOR_AUTHOR));
        sb.append("\n * Date:");
        sb.append(sdf.format(new Date()));
        sb.append("\n */");
        return sb.toString();
    }
}
