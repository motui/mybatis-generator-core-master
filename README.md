# MyBatis-Generator-core-master

```
说明:
1.添加中文注释（数据库中的备注信息），支持MYSQL和ORACLE
2.添加自定义XML Mapper名称前缀和后缀。例如：数据库表为user xmlMapperPrefix设置为mybatis-mapper-,
xmlMapperSuffix不设置,文件名称为:mybatis-mapper-user.xml;默认:userMapper.xml
3.添加自定义Mapper后缀(Dao)。例如：数据库表为user   mapperSuffix设置为Dao,
文件名称为userDao,默认:userMapper
4.添加注释作者
5.添加文件头注释
6.添加Spring注解控制
6.添加MyBatisServicePlugin插件
7.添加MyBatisBizAndServicePlugin插件

代码基于官方源码1.3.5
备注:部分代码参考网上教程

```
---------

```
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE generatorConfiguration
        PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
        "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd" >

<generatorConfiguration>

    <!-- 指定数据连接驱动jar地址 -->
    <classPathEntry location="C:\Users\motui\.m2\repository\mysql\mysql-connector-java\5.1.41\mysql-connector-java-5.1.41.jar"/>

    <!-- 一个数据库一个context -->
    <context id="testTables" targetRuntime="MyBatis3">
        <!-- 自动识别数据库关键字，默认false，如果设置为true，根据SqlReservedWords中定义的关键字列表；
        一般保留默认值，遇到数据库关键字（Java关键字），使用columnOverride覆盖
        -->
        <property name="autoDelimitKeywords" value="false"/>
        <!-- 生成的Java文件的编码 -->
        <property name="javaFileEncoding" value="UTF-8"/>
        <!-- 格式化java代码 -->
        <property name="javaFormatter" value="org.mybatis.generator.api.dom.DefaultJavaFormatter"/>
        <!-- 格式化XML代码 -->
        <property name="xmlFormatter" value="org.mybatis.generator.api.dom.DefaultXmlFormatter"/>

        <!--开启抑制类型的警告信息-->
        <property name="suppressTypeWarnings" value="true"/>

        <!--是否生成Spring注解,默认false-->
        <property name="enableSpringAnnotation" value="false"/>

        <!-- 插件 ：其他插件参考http://www.jianshu.com/p/1b826d43dbaf-->
        <!--插件 ：用来给Java模型生成equals和hashcode方法-->
        <!--<plugin type="org.mybatis.generator.plugins.EqualsHashCodePlugin"/>-->
        <!--插件 ：用来为生成的Java模型类添加序列化接口-->
        <plugin type="org.mybatis.generator.plugins.SerializablePlugin"/>
        <!--插件 ：生成的Java模型创建一个toString方法-->
        <plugin type="org.mybatis.generator.plugins.ToStringPlugin"/>

        <!-- service层插件 -->
        <plugin type="org.mybatis.generator.plugins.MybatisServicePlugin">
            <property name="targetPackage" value="com.motui.service"/>
            <property name="implementationPackage" value="com.motui.service.impl"/>
            <property name="targetProject" value="THIS_CONFIGURATION_IS_NOT_REQUIRED"/>
            <!--以下属性为可选属性-->
            <property name="enableInsert" value="true"/>
            <property name="enableInsertSelective" value="true"/>
            <property name="enableUpdateByPrimaryKey" value="true"/>
            <property name="enableUpdateByPrimaryKeySelective" value="true"/>
            <property name="enableDeleteByPrimaryKey" value="true"/>
            <property name="enableDeleteByExample" value="false"/>
            <property name="enableUpdateByExampleSelective" value="false"/>
            <property name="enableUpdateByExample" value="false"/>
        </plugin>

        <!--Biz和Service层插件-->
        <plugin type="org.mybatis.generator.plugins.MybatisBizAndServicePlugin">
            <property name="targetPackage" value="com.motui.service"/>
            <property name="implementationPackage" value="com.motui.service.impl"/>
            <property name="targetProject" value="THIS_CONFIGURATION_IS_NOT_REQUIRED"/>
            <property name="bizPackage" value="com.motui.biz"/>
            <!--以下属性为可选属性-->
            <property name="enableInsert" value="true"/>
            <property name="enableInsertSelective" value="true"/>
            <property name="enableUpdateByPrimaryKey" value="true"/>
            <property name="enableUpdateByPrimaryKeySelective" value="true"/>
            <property name="enableDeleteByPrimaryKey" value="true"/>
            <property name="enableDeleteByExample" value="false"/>
            <property name="enableUpdateByExampleSelective" value="false"/>
            <property name="enableUpdateByExample" value="false"/>
        </plugin>

        <!-- 注释 -->
        <commentGenerator>
            <!-- 是否去除自动生成的注释 true：是 ： false:否 -->
            <property name="suppressAllComments" value="false"/>
            <!-- 是否给实体类生成的备注的注释 true：是 ： 默认为false:否 -->
            <property name="addRemarkComments" value="true"/>
            <!-- 是否去掉注释代时间戳 true：是 ： false:否,默认yyyy-MM-dd HH:mm:ss-->
            <property name="suppressDate" value="false"/>
            <property name="dateFormat" value="yyyy-MM-dd HH:mm:ss"/>

            <!-- 自己添加的参数属性:数据表字段的get、set方法是否添加final关键字,默认为true -->
            <property name="addMethodFinal" value="true"/>
            <!-- 文件作者 -->
            <property name="author" value="motui-test"/>
        </commentGenerator>

        <!--数据库连接的信息：驱动类、连接地址、用户名、密码 -->
        <jdbcConnection driverClass="com.mysql.jdbc.Driver"
                        connectionURL="jdbc:mysql://127.0.0.1:test"
                        userId="root"
                        password="123456">
        </jdbcConnection>

        <!-- 类型转换 -->
        <javaTypeResolver>
            <!-- 默认false，把JDBC DECIMAL 和 NUMERIC 类型解析为 Integer，为 true时把JDBC DECIMAL 和
                NUMERIC 类型解析为java.math.BigDecimal -->
            <property name="forceBigDecimals" value="false"/>
        </javaTypeResolver>

        <!-- 生成实体类的位置 -->
        <javaModelGenerator targetPackage="com.motui.dto"
                            targetProject="THIS_CONFIGURATION_IS_NOT_REQUIRED">
            <!--  for MyBatis3/MyBatis3Simple
                自动为每一个生成的类创建一个构造方法，构造方法包含了所有的field；而不是使用setter；
            -->
            <property name="constructorBased" value="false"/>

            <!-- 是否在当前路径下新加一层schema,
                eg：false路径com.motui.entity,
                    true:com.motui.entity.[schemaName]
             -->
            <property name="enableSubPackages" value="true"/>
            <!-- 是否针对string类型的字段在set/get的时候进行trim调用:清理前后的空格 -->
            <property name="trimStrings" value="true"/>
        </javaModelGenerator>

        <!-- 生成mapper xml文件 -->
        <sqlMapGenerator targetPackage="mapper"
                         targetProject="THIS_CONFIGURATION_IS_NOT_REQUIRED">
            <!-- 解释同上:生成实体类的位置 -->
            <property name="enableSubPackages" value="true"/>
            <!--如果前缀和后缀不设置或者为空，默认使用tableNameMapper.xml，否则前缀+tableName+后缀.xml-->
            <!--前缀-->
            <property name="xmlMapperPrefix" value="mybatis-mapper-"/>
            <!--后缀-->
            <property name="xmlMapperSuffix" value=""/>
        </sqlMapGenerator>


        <!-- 对于mybatis来说，即生成Mapper接口，注意，如果没有配置该元素，那么默认不会生成Mapper接口
           targetPackage/targetProject:同javaModelGenerator
           type：选择怎么生成mapper接口（在MyBatis3/MyBatis3Simple下）：
               1，ANNOTATEDMAPPER：会生成使用Mapper接口+Annotation的方式创建（SQL生成在annotation中），不会生成对应的XML；
               2，MIXEDMAPPER：使用混合配置，会生成Mapper接口，并适当添加合适的Annotation，但是XML会生成在XML中；
               3，XMLMAPPER：会生成Mapper接口，接口完全依赖XML；
           注意，如果context是MyBatis3Simple：只支持ANNOTATEDMAPPER和XMLMAPPER
       -->
        <!-- 生成mapper接口生成的位置 -->
        <javaClientGenerator type="XMLMAPPER"
                             targetPackage="com.motui.dao"
                             targetProject="THIS_CONFIGURATION_IS_NOT_REQUIRED">
            <!-- 解释同上:生成实体类的位置 -->
            <property name="enableSubPackages" value="true"/>
            <!--后缀,默认为Mapper-->
            <property name="mapperSuffix" value="Dao"/>

        </javaClientGenerator>

        <!-- mvn mybatis-generator:generate  -->
        <!-- 指定生成的数据库表 -->
        <!-- domainObjectName：指定生成的实体类的文件名 -->
        <table tableName="anchor" domainObjectName="Anchor" enableCountByExample="false" enableDeleteByExample="false" enableSelectByExample="false" enableUpdateByExample="false"/>
    </context>
</generatorConfiguration>

```
