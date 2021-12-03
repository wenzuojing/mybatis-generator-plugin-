package org.wens.mybatis.gererator.plugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.internal.util.JavaBeansUtil;

import java.util.List;

public class EnhanceMapperPlugin extends PluginAdapter {
    @Override
    public boolean validate(List<String> list) {
        return true;
    }



    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        if(introspectedTable.getTargetRuntime() == IntrospectedTable.TargetRuntime.MYBATIS3_DSQL ){

            insertMultipleGenerated(interfaze, introspectedTable);
            updateGenerated(interfaze, introspectedTable);
            deleteGenerated(interfaze, introspectedTable);
            selectByExampleGenerated(interfaze, introspectedTable);
            selectDistinctByExampleGenerated(interfaze, introspectedTable);

            selectManyGenerated(interfaze, introspectedTable);

            if(introspectedTable.getPrimaryKeyColumns().size() == 1 ){

                selectOneGenerated(interfaze, introspectedTable);
                IntrospectedColumn primaryKeyColumn = introspectedTable.getPrimaryKeyColumns().get(0);
                selectByPrimaryKeysGenerated(interfaze, introspectedTable, primaryKeyColumn);
                selectByPrimaryKeyGenerated(interfaze, introspectedTable, primaryKeyColumn);

            }
        }


        return true ;
    }

    private void selectManyGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
        interfaze.addImportedType(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.select.SelectDSLCompleter"));
        interfaze.addImportedType(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.util.mybatis3.MyBatis3Utils"));
        Method method = new Method();
        method.setDefault(true);
        method.setName("selectMany");
        method.addParameter(new Parameter(new FullyQualifiedJavaType("SelectDSLCompleter"),"completer"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("BasicColumn...")," selectList"));
        method.addBodyLine("        return MyBatis3Utils.selectOne(this::selectMany, selectList, "+getTable(introspectedTable)+", completer);");
        method.setReturnType(new FullyQualifiedJavaType("List<"+introspectedTable.getBaseRecordType()+">"));
        interfaze.addMethod(method);

        Method method2 = new Method();
        method2.setDefault(true);
        method2.setName("selectMany");
        method2.addParameter(new Parameter(new FullyQualifiedJavaType("SelectDSLCompleter"),"completer"));
        method2.addBodyLine("        return this.selectMany(completer,new BasicColumn[]{"+getDefaultSelectList(introspectedTable)+"});");
        method2.setReturnType(new FullyQualifiedJavaType("List<"+introspectedTable.getBaseRecordType()+">"));
        interfaze.addMethod(method2);

    }

    private void selectOneGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
        interfaze.addImportedType(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.select.SelectDSLCompleter"));
        interfaze.addImportedType(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.util.mybatis3.MyBatis3Utils"));
        Method method = new Method();
        method.setDefault(true);
        method.setName("selectOne");
        method.addParameter(new Parameter(new FullyQualifiedJavaType("SelectDSLCompleter"),"completer"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("BasicColumn...")," selectList"));
        method.addBodyLine("        return MyBatis3Utils.selectOne(this::selectOne, selectList, "+getTable(introspectedTable)+", completer);");
        method.setReturnType(new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()));
        interfaze.addMethod(method);

        Method method2 = new Method();
        method2.setDefault(true);
        method2.setName("selectOne");
        method2.addParameter(new Parameter(new FullyQualifiedJavaType("SelectDSLCompleter"),"completer"));
        method2.addBodyLine("        return this.selectOne(completer,new BasicColumn[]{"+getDefaultSelectList(introspectedTable)+"});");
        method2.setReturnType(new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()));
        interfaze.addMethod(method2);

    }

    private void selectDistinctByExampleGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
        interfaze.addImportedType(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.BasicColumn"));
        Method method = new Method();
        method.setDefault(true);
        method.setName("selectDistinctByExample");
        method.addParameter(new Parameter(new FullyQualifiedJavaType("BasicColumn...")," selectList"));

        StringBuilder body = new StringBuilder();
        body.append("        return SelectDSL.selectDistinctWithMapper(this::selectMany, selectList)\n" +
                "                .from("+ getTable(introspectedTable) +");");

        method.addBodyLine(body.toString());
        method.setReturnType(new FullyQualifiedJavaType("QueryExpressionDSL<MyBatis3SelectModelAdapter<List<"+introspectedTable.getBaseRecordType()+">>>"));
        interfaze.addMethod(method);
    }

    private String getTable(IntrospectedTable introspectedTable) {
        return JavaBeansUtil.getValidPropertyName(introspectedTable.getFullyQualifiedTable().getDomainObjectName());
    }

    private void selectByExampleGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
        interfaze.addImportedType(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.BasicColumn"));
        Method method = new Method();
        method.setDefault(true);
        method.setName("selectByExample");
        method.addParameter(new Parameter(new FullyQualifiedJavaType("BasicColumn...")," selectList"));

        StringBuilder body = new StringBuilder();
        body.append("        return SelectDSL.selectWithMapper(this::selectMany, selectList)\n" +
                "                .from("+ getTable(introspectedTable) +");");

        method.addBodyLine(body.toString());
        method.setReturnType(new FullyQualifiedJavaType("QueryExpressionDSL<MyBatis3SelectModelAdapter<List<"+introspectedTable.getBaseRecordType()+">>>"));
        interfaze.addMethod(method);
    }

    private void selectByPrimaryKeyGenerated(Interface interfaze, IntrospectedTable introspectedTable, IntrospectedColumn primaryKeyColumn) {

        interfaze.addImportedType(FullyQualifiedJavaType.getNewListInstance());
        String tableFieldName  = getTable(introspectedTable);
        Method method = new Method();
        method.setDefault(true);
        method.setName("selectByPrimaryKey");
        method.addParameter(new Parameter(new FullyQualifiedJavaType(primaryKeyColumn.getFullyQualifiedJavaType().toString()) ,primaryKeyColumn.getJavaProperty() + "_" ));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("BasicColumn...")," selectList"));
        method.setReturnType(new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()));
        StringBuilder body = new StringBuilder();
        body.append("        return SelectDSL.selectWithMapper(this::selectOne, selectList)\n" +
                "                .from(").append(tableFieldName).append(")\n" +
                "                .where(").append(primaryKeyColumn.getJavaProperty()).append(", isEqualTo(").append(primaryKeyColumn.getJavaProperty()).append("_))\n" +
                "                .build()\n" +
                "                .execute();");
        method.addBodyLine(body.toString());
        interfaze.addMethod(method);
    }

    private void selectByPrimaryKeysGenerated(Interface interfaze, IntrospectedTable introspectedTable, IntrospectedColumn primaryKeyColumn) {

        interfaze.addImportedType(FullyQualifiedJavaType.getNewListInstance());
        interfaze.addImportedType(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.BasicColumn"));
        String tableFieldName  = getTable(introspectedTable);
        Method method = new Method();
        method.setDefault(true);
        method.setName("selectByPrimaryKeys");
        method.addParameter(new Parameter(new FullyQualifiedJavaType("List<" + primaryKeyColumn.getFullyQualifiedJavaType() + ">") ,primaryKeyColumn.getJavaProperty() + "s_" ));
        method.setReturnType(new FullyQualifiedJavaType("List<"+introspectedTable.getBaseRecordType()+">" ));
        StringBuilder body = new StringBuilder();
        body.append("        return this.selectByPrimaryKeys(").append(primaryKeyColumn.getJavaProperty()).append("s_,new BasicColumn[]{").append(getDefaultSelectList(introspectedTable)).append("});");
        method.addBodyLine(body.toString());
        interfaze.addMethod(method);

        Method method2 = new Method();
        method2.setDefault(true);
        method2.setName("selectByPrimaryKeys");
        method2.addParameter(new Parameter(new FullyQualifiedJavaType("List<" + primaryKeyColumn.getFullyQualifiedJavaType() + ">") ,primaryKeyColumn.getJavaProperty() + "s_" ));
        method2.addParameter(new Parameter(new FullyQualifiedJavaType("BasicColumn...")," selectList"));
        method2.setReturnType(new FullyQualifiedJavaType("List<"+introspectedTable.getBaseRecordType()+">" ));
        StringBuilder body2 = new StringBuilder();
        body2.append("        return SelectDSL.selectWithMapper(this::selectMany, selectList)\n" +
                "                .from(").append(tableFieldName).append(")\n" +
                "                .where(").append(primaryKeyColumn.getJavaProperty()).append(", isIn(").append(primaryKeyColumn.getJavaProperty()).append("s_))\n" +
                "                .build()\n" +
                "                .execute();");

        method2.addBodyLine(body2.toString());
        interfaze.addMethod(method2);
    }

    private void insertMultipleGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
        interfaze.addImportedType(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.insert.render.MultiRowInsertStatementProvider"));
        Method method = new Method();
        method.setDefault(false);
        method.setName("insertMultiple");
        method.addAnnotation("@InsertProvider(type=SqlProviderAdapter.class, method=\"insertMultiple\")");
        method.addParameter(new Parameter(new FullyQualifiedJavaType("MultiRowInsertStatementProvider<"+introspectedTable.getBaseRecordType()+">"),"multiRowInsertStatementProvider"));
        method.setReturnType(new FullyQualifiedJavaType("int"));
        interfaze.addMethod(method);

        Method method2 = new Method();
        method2.setDefault(true);
        method2.setName("insertMultiple");
        method2.addParameter(new Parameter(new FullyQualifiedJavaType("List<"+introspectedTable.getBaseRecordType()+">"),"records"));

        StringBuilder body = new StringBuilder();
        body.append("return insertMultiple(SqlBuilder.insertMultiple(records)\n" );
        body.append("                .into("+getTable(introspectedTable)+ ")\n");

        for(IntrospectedColumn ic : introspectedTable.getAllColumns() ){
            body.append("                .map("+ic.getJavaProperty()+").toProperty(\""+ic.getJavaProperty()+"\")\n");
        }

        body.append("                .build()\n");
        body.append("                .render(RenderingStrategy.MYBATIS3));");


        method2.addBodyLine(body.toString());
        method2.setReturnType(new FullyQualifiedJavaType("int"));
        interfaze.addMethod(method2);

    }

    private void updateGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
        interfaze.addImportedType(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.update.UpdateDSLCompleter"));
        interfaze.addImportedType(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.SqlBuilder"));
        interfaze.addImportedType(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.render.RenderingStrategies"));
        Method method = new Method();
        method.setDefault(true);
        method.setName("update");
        method.addParameter(new Parameter(new FullyQualifiedJavaType("UpdateDSLCompleter"),"completer"));
        method.addBodyLine("        return update(completer.apply(SqlBuilder.update("+introspectedTable.getMyBatisDynamicSqlSupportType()+"."+getTable(introspectedTable)+")).build().render(RenderingStrategies.MYBATIS3));");
        method.setReturnType(new FullyQualifiedJavaType("int"));
        interfaze.addMethod(method);
    }

    private void deleteGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
        interfaze.addImportedType(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.delete.DeleteDSLCompleter"));
        interfaze.addImportedType(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.SqlBuilder"));
        interfaze.addImportedType(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.render.RenderingStrategies"));
        Method method = new Method();
        method.setDefault(true);
        method.setName("delete");
        method.addParameter(new Parameter(new FullyQualifiedJavaType("DeleteDSLCompleter"),"completer"));
        method.addBodyLine("        return delete(completer.apply(SqlBuilder.deleteFrom("+introspectedTable.getMyBatisDynamicSqlSupportType()+"."+getTable(introspectedTable)+")).build().render(RenderingStrategies.MYBATIS3));");
        method.setReturnType(new FullyQualifiedJavaType("int"));
        interfaze.addMethod(method);
    }

    private String getDefaultSelectList(IntrospectedTable introspectedTable){
        StringBuilder columns  = new StringBuilder();
        List<IntrospectedColumn> allColumns = introspectedTable.getAllColumns();
        for(IntrospectedColumn ic : allColumns ){
            if( columns.length() > 0 ){
                columns.append(",");
            }
            columns.append(ic.getJavaProperty());
        }
        return columns.toString();
    }
}
