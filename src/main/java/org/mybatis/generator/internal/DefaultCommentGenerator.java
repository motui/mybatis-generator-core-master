/**
 * Copyright 2006-2016 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mybatis.generator.internal;

import static org.mybatis.generator.internal.util.StringUtility.isTrue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.InnerClass;
import org.mybatis.generator.api.dom.java.InnerEnum;
import org.mybatis.generator.api.dom.java.JavaElement;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.config.MergeConstants;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.util.StringUtility;

/**
 * The Class DefaultCommentGenerator.
 * 修改注释
 *
 * @author Jeff Butler
 */
public class DefaultCommentGenerator implements CommentGenerator {

    /**
     * The properties.
     */
    private Properties properties;

    /**
     * The suppress date.
     */
    private boolean suppressDate;

    /**
     * The suppress all comments.
     */
    private boolean suppressAllComments;

    /**
     * The addition of table remark's comments.
     * If suppressAllComments is true, this option is ignored
     */
    private boolean addRemarkComments;

    private SimpleDateFormat dateFormat;

    /**
     * 是否在get、set方法里添加final关键字
     * 默认：true
     */
    private boolean addMethodFinal;

    /**
     * 作者信息配置
     */
    private String author;

    /**
     * Date 格式化
     */
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Instantiates a new default comment generator.
     */
    public DefaultCommentGenerator() {
        super();
        this.properties = new Properties();
        this.suppressDate = false;
        this.suppressAllComments = false;
        this.addRemarkComments = false;
        this.addMethodFinal = true;
        this.author = "motui";
    }

    /**
     * 给Java文件加注释，这个注释是在文件的顶部，也就是package上面
     */
    public void addJavaFileComment(CompilationUnit compilationUnit) {
        // add no file level comments by default
        compilationUnit.addFileCommentLine("/*");
        compilationUnit.addFileCommentLine(" * " + compilationUnit.getType().getShortName() + ".java");
        compilationUnit.addFileCommentLine(" * Copyright(C) " + this.author);
        compilationUnit.addFileCommentLine(" * All rights reserved.");
        compilationUnit.addFileCommentLine(" * " + sdf.format(new Date()) + " Created");
        compilationUnit.addFileCommentLine(" */ ");
    }

    /**
     * Adds a suitable comment to warn users that the element was generated, and when it was generated.
     * mapper.xml中不生成的注释
     * @param xmlElement the xml element
     */
    public void addComment(XmlElement xmlElement) {
        //不生成注释
    }


    public void addRootComment(XmlElement rootElement) {
        // add no document level comments by default
    }

    public void addConfigurationProperties(Properties properties) {
        this.properties.putAll(properties);

        this.suppressDate = isTrue(properties
                .getProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_DATE));

        this.suppressAllComments = isTrue(properties
                .getProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_ALL_COMMENTS));

        this.addRemarkComments = isTrue(properties
                .getProperty(PropertyRegistry.COMMENT_GENERATOR_ADD_REMARK_COMMENTS));

        this.addMethodFinal = isTrue(properties
                .getProperty(PropertyRegistry.COMMENT_GENERATOR_ADD_METHOD_FINAL));
        String dateFormatString = properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_DATE_FORMAT);
        if (StringUtility.stringHasValue(dateFormatString)) {
            dateFormat = new SimpleDateFormat(dateFormatString);
        }
        String authorString = properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_AUTHOR);
        if (StringUtility.stringHasValue(authorString)) {
            author = authorString;
        }
    }

    /**
     * This method adds the custom javadoc tag for. You may do nothing if you do not wish to include the Javadoc tag -
     * however, if you do not include the Javadoc tag then the Java merge capability of the eclipse plugin will break.
     *
     * @param javaElement       the java element
     * @param markAsDoNotDelete the mark as do not delete
     */
    protected void addJavadocTag(JavaElement javaElement,
                                 boolean markAsDoNotDelete) {
        javaElement.addJavaDocLine(" *"); //$NON-NLS-1$
        StringBuilder sb = new StringBuilder();
        sb.append(" * "); //$NON-NLS-1$
        // sb.append(MergeConstants.NEW_ELEMENT_TAG);
        if (markAsDoNotDelete) {
            sb.append(" do_not_delete_during_merge"); //$NON-NLS-1$
        }
        sb.append("@author " + this.author);
        String s = getDateString();
        if (s != null) {
            sb.append("\n * Date:");
            sb.append(s);
        }
        javaElement.addJavaDocLine(sb.toString());
    }

    /**
     * This method returns a formated date string to include in the Javadoc tag
     * and XML comments. You may return null if you do not want the date in
     * these documentation elements.
     *
     * @return a string representing the current timestamp, or null
     */
    protected String getDateString() {
        if (suppressDate) {
            return null;
        } else if (dateFormat != null) {
            return dateFormat.format(new Date());
        } else {
            //可自定义时间格式
            return sdf.format(new Date());
        }
    }

    /**
     * Java类的类注释。
     *
     * @param innerClass
     * @param introspectedTable
     */
    public void addClassComment(InnerClass innerClass,
                                IntrospectedTable introspectedTable) {
        if (suppressAllComments) {
            return;
        }
        innerClass.addJavaDocLine("/*");
        innerClass.addJavaDocLine(" * " + introspectedTable.getFullyQualifiedTable().getRemarks());
        innerClass.addJavaDocLine(" * ");
        addJavadocTag(innerClass, false);
        innerClass.addJavaDocLine(" */ ");
    }

    @Override
    public void addModelClassComment(TopLevelClass topLevelClass,
                                     IntrospectedTable introspectedTable) {
        //类注释的添加
        if (suppressAllComments || !addRemarkComments) {
            return;
        }

        topLevelClass.addJavaDocLine("/** "); //$NON-NLS-1$

        String remarks = introspectedTable.getFullyQualifiedTable().getRemarks();
        // String remarks = introspectedTable.getRemarks();
        if (addRemarkComments && StringUtility.stringHasValue(remarks)) {
            String[] remarkLines = remarks.split(System.getProperty("line.separator"));  //$NON-NLS-1$
            for (String remarkLine : remarkLines) {
                topLevelClass.addJavaDocLine(" * " + remarkLine + ":" + introspectedTable.getFullyQualifiedTable());  //$NON-NLS-1$
            }
        }
        addJavadocTag(topLevelClass, false);
        topLevelClass.addJavaDocLine(" */"); //$NON-NLS-1$
    }

    /**
     * 枚举的注释
     * @param innerEnum
     *            the inner enum
     * @param introspectedTable
     */
    public void addEnumComment(InnerEnum innerEnum,
                               IntrospectedTable introspectedTable) {
        if (suppressAllComments) {
            return;
        }

        StringBuilder sb = new StringBuilder();

        innerEnum.addJavaDocLine("/**"); //$NON-NLS-1$
        innerEnum
                .addJavaDocLine(" * This enum was generated by MyBatis Generator."); //$NON-NLS-1$

        sb.append(" * This enum corresponds to the database table "); //$NON-NLS-1$
        sb.append(introspectedTable.getFullyQualifiedTable());
        innerEnum.addJavaDocLine(sb.toString());
        innerEnum.addJavaDocLine(" */"); //$NON-NLS-1$
    }

    /**
     * Java属性注释
     *
     * @param field              the field
     * @param introspectedTable  the introspected table
     * @param introspectedColumn
     */
    public void addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        if (suppressAllComments) {
            return;
        }

        if (introspectedColumn.getRemarks() != null && !"".equals(introspectedColumn.getRemarks())) {
            field.addJavaDocLine("/**");
            field.addJavaDocLine(" * " + introspectedColumn.getRemarks());
            //对应表名称.对应表字段名称
            field.addJavaDocLine(" * 表字段:" + introspectedTable.getFullyQualifiedTable() + "." + introspectedColumn.getActualColumnName());
            field.addJavaDocLine(" */");

        }
    }

    /**
     * Java属性注释
     *
     * @param field             the field
     * @param introspectedTable
     */
    public void addFieldComment(Field field, IntrospectedTable introspectedTable) {
        if (suppressAllComments) {
            return;
        }

        StringBuilder sb = new StringBuilder();

        if ("distinct".equals(field.getName())) {
            sb.append(" 过滤重复数据");
        } else if ("orderByClause".equals(field.getName())) {
            sb.append(" 排序字段");
        } else if ("oredCriteria".equals(field.getName())) {
            sb.append(" 查询条件");
        } else if ("serialVersionUID".equals(field.getName())) {
            sb.append("串行版本ID");
        }
        if (sb.length() > 0) {
            field.addJavaDocLine("//" + sb.toString());
        }
    }

    /**
     * mapper接口中的注释
     *
     * @param method            the method
     * @param introspectedTable
     */
    public void addGeneralMethodComment(Method method,
                                        IntrospectedTable introspectedTable) {
        if (suppressAllComments) {
            return;
        }
        method.setFinal(addMethodFinal);
        StringBuilder sb = new StringBuilder();
        method.addJavaDocLine("/**");
        sb.append(" * ");
        if (method.isConstructor()) {
            sb.append(" 构造查询条件");
        }
        String method_name = method.getName();
        if ("setOrderByClause".equals(method_name)) {
            sb.append(" 设置排序字段");
        } else if ("setDistinct".equals(method_name)) {
            sb.append(" 设置过滤重复数据");
        } else if ("getOredCriteria".equals(method_name)) {
            sb.append(" 获取当前的查询条件实例");
        } else if ("isDistinct".equals(method_name)) {
            sb.append(" 是否过滤重复数据");
        } else if ("getOrderByClause".equals(method_name)) {
            sb.append(" 获取排序字段");
        } else if ("createCriteria".equals(method_name)) {
            sb.append(" 创建一个查询条件");
        } else if ("createCriteriaInternal".equals(method_name)) {
            sb.append(" 内部构建查询条件对象");
        } else if ("clear".equals(method_name)) {
            sb.append(" 清除查询条件");
        } else if ("countByExample".equals(method_name)) {
            sb.append(" 根据指定的条件获取数据库记录数");
        } else if ("deleteByExample".equals(method_name)) {
            sb.append(" 根据指定的条件删除数据库符合条件的记录");
        } else if ("deleteByPrimaryKey".equals(method_name)) {
            sb.append(" 根据主键删除数据库的记录");
        } else if ("insert".equals(method_name)) {
            sb.append(" 新写入数据库记录");
        } else if ("insertSelective".equals(method_name)) {
            sb.append(" 动态字段,写入数据库记录");
        } else if ("selectByExample".equals(method_name)) {
            sb.append(" 根据指定的条件查询符合条件的数据库记录");
        } else if ("selectByPrimaryKey".equals(method_name)) {
            sb.append(" 根据指定主键获取一条数据库记录");
        } else if ("updateByExampleSelective".equals(method_name)) {
            sb.append(" 动态字段,根据指定的条件来更新符合条件的数据库记录");
        } else if ("updateByExample".equals(method_name)) {
            sb.append(" 根据指定的条件来更新符合条件的数据库记录");
        } else if ("updateByPrimaryKeySelective".equals(method_name)) {
            sb.append(" 动态字段,根据主键来更新符合条件的数据库记录");
        } else if ("updateByPrimaryKey".equals(method_name)) {
            sb.append(" 根据主键来更新符合条件的数据库记录");
        }
        sb.append(",");
        // sb.append(introspectedTable.getFullyQualifiedTable());
        method.addJavaDocLine(sb.toString());

        final List<Parameter> parameterList = method.getParameters();
        if (!parameterList.isEmpty()) {
            method.addJavaDocLine(" *"); //$NON-NLS-1$
            if ("or".equals(method_name)) {
                sb.append(" 增加或者的查询条件,用于构建或者查询");
            }
        } else {
            if ("or".equals(method_name)) {
                sb.append(" 创建一个新的或者查询条件");
            }
        }
        for (Parameter parameter : parameterList) {
            sb.setLength(0);
            sb.append(" * @param "); //$NON-NLS-1$
            String paramterName = parameter.getName();
            sb.append(paramterName);
            if ("orderByClause".equals(paramterName)) {
                sb.append(" 排序字段"); //$NON-NLS-1$
            } else if ("distinct".equals(paramterName)) {
                sb.append(" 是否过滤重复数据");
            } else if ("criteria".equals(paramterName)) {
                sb.append(" 过滤条件实例");
            }
            method.addJavaDocLine(sb.toString());
        }
        method.addJavaDocLine(" */"); //$NON-NLS-1$
    }

    /**
     * Getter 方法注释
     *
     * @param method             the method
     * @param introspectedTable  the introspected table
     * @param introspectedColumn
     */
    public void addGetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        //不设置注释
    }

    /**
     * Setter 方法注释
     * @param method
     *            the method
     * @param introspectedTable
     *            the introspected table
     * @param introspectedColumn
     */
    public void addSetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        // 不设置注释
    }

    /**
     * 删除生成Criteria对象的注释信息的注释
     * @param innerClass
     *            the inner class
     * @param introspectedTable
     *            the introspected table
     * @param markAsDoNotDelete
     */
    public void addClassComment(InnerClass innerClass,
                                IntrospectedTable introspectedTable, boolean markAsDoNotDelete) {
        if (suppressAllComments) {
            return;
        }

        // 生成Criteria对象的注释信息的注释
        StringBuilder sb = new StringBuilder();
        innerClass.addJavaDocLine("/**");
        sb.append(" * ").append(introspectedTable.getFullyQualifiedTable().getRemarks())
                .append(introspectedTable.getFullyQualifiedTable()).append("的映射实体");
        innerClass.addJavaDocLine(sb.toString());
        addJavadocTag(innerClass,markAsDoNotDelete);
        innerClass.addJavaDocLine(" */");

    }

    /**
     * 用于非实体类Criteria的注释
     *
     * @param javaElement
     */
    @Override
    public void addExampleClassComment(JavaElement javaElement) {
        if (suppressAllComments) {
            return;
        }
        javaElement.addJavaDocLine("/**");
        javaElement.addJavaDocLine(" * 由Generator自动生成");
        addJavadocTag(javaElement, false);
        javaElement.addJavaDocLine(" */");
    }
}
