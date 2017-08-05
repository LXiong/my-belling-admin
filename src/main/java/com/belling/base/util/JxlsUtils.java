package com.belling.base.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jxls.reader.ReaderBuilder;
import net.sf.jxls.reader.XLSReadStatus;
import net.sf.jxls.reader.XLSReader;

import org.jxls.common.Context;
import org.jxls.expression.JexlExpressionEvaluator;
import org.jxls.transform.Transformer;
import org.jxls.util.JxlsHelper;

import com.belling.admin.model.Role;

/**
 * 
 * @author klguang
 *
 */
public class JxlsUtils{
	
	private static final String TEMPLATE_PATH="jxls-template";
	
	public static void exportExcel(InputStream is, OutputStream os, Map<String, Object> model) throws IOException{
        Context context = new Context();
        if (model != null) {
            for (String key : model.keySet()) {
                context.putVar(key, model.get(key));
            }
        }
        JxlsHelper jxlsHelper = JxlsHelper.getInstance();
        Transformer transformer  = jxlsHelper.createTransformer(is, os);
        JexlExpressionEvaluator evaluator = (JexlExpressionEvaluator)transformer.getTransformationConfig().getExpressionEvaluator();
        Map<String, Object> funcs = new HashMap<String, Object>();
        funcs.put("utils", new JxlsUtils());    //添加自定义功能
        evaluator.getJexlEngine().setFunctions(funcs);
        jxlsHelper.processTemplate(context, transformer);
	}

    public static void exportExcel(File xls, File out, Map<String, Object> model) throws FileNotFoundException, IOException {
            exportExcel(new FileInputStream(xls), new FileOutputStream(out), model);
    }
    
    public static void exportExcel(String templateName, OutputStream os, Map<String, Object> model) throws FileNotFoundException, IOException {
    	File template = getTemplate(templateName);
    	if(template!=null){
        	exportExcel(new FileInputStream(template), os, model);	
    	}
    }
    
    
    //获取jxls模版文件

    public static File getTemplate(String name){
        String templatePath = JxlsUtils.class.getClassLoader().getResource(TEMPLATE_PATH).getPath();
        File template = new File(templatePath, name);
        if(template.exists()){
            return template;
        }
        return null;
    }	
	
    // 日期格式化
    public String dateFmt(Date date, String fmt) {
        if (date == null) {
            return "";
        }
        try {
            SimpleDateFormat dateFmt = new SimpleDateFormat(fmt);
            return dateFmt.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    
    // if判断
    public Object ifelse(boolean b, Object o1, Object o2) {
        return b ? o1 : o2;
    }
    
    
    /** 
     * 使用jxls解析导入的Excel 
     * @param path 导入文件路径 
     * @return List<VideoInfo> 导入对象集合 
     */  
    public static <T> List<T> getExcelData(File path,File inputXML,Class<T> clazz){  
        List<T> objInfoList = new ArrayList<T>();  
        try {  
            XLSReader mainReader = ReaderBuilder.buildFromXML( inputXML );  
            InputStream inputXLS = new BufferedInputStream(new FileInputStream(path));  
            Map<String,Object> beans = new HashMap<String,Object>();  
            //T t = clazz.newInstance();
            //beans.put("objInfo", t);  
            beans.put("objList", objInfoList);  
            XLSReadStatus readStatus = mainReader.read( inputXLS, beans);  
            if(readStatus.isStatusOK()){  
                System.out.println("jxls读取Excel成功！");  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return objInfoList;  
    }
    
    
}