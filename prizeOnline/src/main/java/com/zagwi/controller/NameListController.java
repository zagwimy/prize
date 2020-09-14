package com.zagwi.controller;

import com.zagwi.domain.*;
import com.zagwi.service.StudentService;
import com.zagwi.utils.POIUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/namelist")
public class NameListController {
    
    @Autowired
    private StudentService studentService;

    @RequestMapping("/upload")
    public Result upload(@RequestParam("excelFile") MultipartFile excelFile){
        try {
            List<String[]> list = POIUtils.readExcel(excelFile);//使用POI解析表格数据
            List<Student> data = new ArrayList<>();
            for (String[] strings : list) {

                String number = strings[0];
                String name = strings[1];

                Student s = new Student();
                s.setName(name);
                s.setNumber(number);
                if(strings.length > 2){
                    String remark = strings[2];
                    s.setRemark(remark);
                }

                data.add(s);
            }
            List<Student> students = studentService.add(data);

            return new Result(true, "上传成功",students);
        } catch (IOException e) {
            e.printStackTrace();
            //文件解析失败
            return new Result(false, "上传失败");
        }
    }

    @RequestMapping("/findAll")
    public Result findAll(){
        try {
            List<Student> students = studentService.findAll();

            return new Result(true, "查询成功",students);
        } catch (Exception e) {
            e.printStackTrace();
            //文件解析失败
            return new Result(false, "查询失败");
        }
    }
    @RequestMapping("/getRollResult")
    public Result getRollResult(){
        try {
            RollResult rollResult = studentService.getRollResult();
            return new Result(true, "摇号成功",rollResult);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false, "服务器异常，摇号失败，请稍后重试");
        }
    }

    @RequestMapping("/pickStudents")
    public Result pickStudents(int version,String[] pickedNumbers,int batch){
        try {
            studentService.pickStudents(version,pickedNumbers,batch);
            return new Result(true, "保存成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false, "服务器异常，保存失败");
        }
    }

    @RequestMapping("/findHistoryByVersion")
    public Result findHistoryByVersion(int version){
        try {
            List<History> histories = studentService.findHistoryByVersion(version);
            return new Result(true, "查询成功",histories);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false, "服务器异常，查询失败");
        }
    }
    @RequestMapping("/deleteHistory")
    public Result deleteHistory(int version){
        try {
            studentService.deleteHistory(version);
            return new Result(true, "删除成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false, "服务器异常，删除失败");
        }
    }
    @RequestMapping("/deleteStudentList")
    public Result deleteStudentList(){
        try {
            studentService.deleteStudentList();
            return new Result(true, "删除成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false, "服务器异常，删除失败");
        }
    }

    @RequestMapping("/saveHistoryBackup")
    public Result saveHistoryBackup(@RequestBody String historyBackup,int version){
        try {
            studentService.saveHistoryBackup(historyBackup,version);
            return new Result(true, "保存成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false, "服务器异常，保存失败");
        }
    }

    @RequestMapping(value = "/findHistoryBackup",produces = {"application/json;charset=utf-8"})
    public String findHistoryBackup(int version){
        try {
            String historyBackup = studentService.findHistoryBackup(version);
            return historyBackup;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @RequestMapping("/findHistoryList")
    public Result findHistoryList(){
        try {
            List<History> historyList = studentService.findHistoryList();
            return new Result(true, "查询成功",historyList);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false, "服务器异常，查询失败");
        }
    }
    @RequestMapping("/exportExcel")
    public void exportExcel(int version, HttpServletRequest request, HttpServletResponse response){
        try {
            List<History> histories = studentService.findHistoryByVersion(version);

            String filePath = request.getSession().getServletContext().getRealPath("/template")+ File.separator+"export.xlsx";
            //基于提供的Excel模板文件在内存中创建一个Excel表格对象
            XSSFWorkbook excel = new XSSFWorkbook(new FileInputStream(new File(filePath)));
            //读取第一个工作表
            XSSFSheet sheet = excel.getSheetAt(0);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (int i = 0; i < histories.size(); i++) {
                XSSFRow row = sheet.createRow(i+1);
                row.createCell(0).setCellValue(histories.get(i).getNumber());
                row.createCell(1).setCellValue(histories.get(i).getName());
                row.createCell(2).setCellValue(histories.get(i).getBatch()+"");
                row.createCell(3).setCellValue(sdf.format(histories.get(i).getInsertTime()));
            }
            
            //使用输出流进行表格下载,基于浏览器作为客户端下载
            OutputStream out = response.getOutputStream();
            response.setContentType("application/vnd.ms-excel");//代表的是Excel文件类型
            response.setHeader("content-Disposition", "attachment;filename=export.xlsx");//指定以附件形式进行下载
            excel.write(out);

            out.flush();
            out.close();
            excel.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // System.out.println(System.getProperty("user.dir"));
    }
}
