package com.zagwi.service.impl;

import com.zagwi.dao.StudentDao;
import com.zagwi.domain.History;
import com.zagwi.domain.RollResult;
import com.zagwi.domain.Student;
import com.zagwi.service.StudentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentDao studentDao;

    @Override
    public List<Student> add(List<Student> data) {
//        for (Student student : data) {
//            studentDao.add(student);
//        }
        studentDao.addBatch(data);
        return studentDao.findAll();
    }

    @Override
    public List<Student> findAll() {
        return studentDao.findAll();
    }

    @Override
    public RollResult getRollResult() {
        List<Student> pickedStudents = new ArrayList<>();
        int remaining = 300;
        RollResult result = new RollResult();
        result.setAllNumbers(studentDao.findAllNumbers());
        List<Student> whitelist = studentDao.findWhitelist();
        pickedStudents.addAll(whitelist);
        System.out.println("白名单列表："+whitelist);
        remaining -= whitelist.size();
        System.out.println("剩余"+remaining+"人从待定列表中抽取...");
        // 获取待定列表
        List<Student> undetermined = studentDao.findUndetermined();
        System.out.println("待定列表："+undetermined);
        //从待定列表补齐剩余人员
        Random random = new Random();
        while (remaining-- > 0){
            int index = random.nextInt(undetermined.size());
            //抽取一个待定学生
            Student remove = undetermined.remove(index);
            System.out.println("从待定列表中抽取学生："+remove+",还剩余"+remaining+"人从待定列表中抽取...");
            pickedStudents.add(remove);
        }
        System.out.println("抽取完成，共抽取学生"+pickedStudents.size()+"人："+pickedStudents);
        // 抽取完成，将学生存入临时表中
        int version = studentDao.getMaxVersion() + 1;
        List<History> historiesForInsert = new ArrayList<>();
        for (Student pickedStudent : pickedStudents) {
            History history = new History();
            history.setName(pickedStudent.getName());
            history.setNumber(pickedStudent.getNumber());
            history.setVersion(version);
            // studentDao.insertHistory(history);
            historiesForInsert.add(history);
        }
        studentDao.insertHistoryBatchMode(historiesForInsert);
        List<History> historyByVersion = studentDao.findHistoryByVersion(version);
        // 最终抽取学生的列表洗牌前
        System.out.println("最终抽取学生的列表洗牌前:" + historyByVersion);
        Collections.shuffle(historyByVersion);
        // 最终抽取学生的列表洗牌后
        System.out.println("最终抽取学生的列表洗牌后:" + historyByVersion);
        result.setRandomResult(historyByVersion);
        result.setVersion(version);
        return result;
    }

    @Override
    public void pickStudents(int version, String[] pickedNumbers, int batch) {
        if(pickedNumbers == null || pickedNumbers.length ==0){
            return;
        }
        List<History> historyByVersion = studentDao.findHistoryByVersion(version);
        // 找出数据库中已有number
        List<String> numbersForUpdate = new ArrayList<>();
        List<History> historiesForInsert = new ArrayList<>();
        for (String pickedNumber : pickedNumbers) {
            boolean flag = false;
            for (History history : historyByVersion) {
                if(StringUtils.equals(pickedNumber,history.getNumber())){
                    flag = true;
                    break;
                }
            }
            if(flag){// 库中存在，更新
                numbersForUpdate.add(pickedNumber);
            }else{

                History history = new History();
                history.setNumber(pickedNumber);
                history.setVersion(version);
                history.setBatch(batch);
                historiesForInsert.add(history);
            }
        }
        if(numbersForUpdate.size() >0) {
            studentDao.updateHistory(version, numbersForUpdate.toArray(new String[numbersForUpdate.size()]), batch);
        }
        if(historiesForInsert.size() > 0){
            studentDao.insertHistoryBatchMode(historiesForInsert);
        }
    }

    @Override
    public List<History> findHistoryByVersion(int version) {
        return studentDao.findPickedHistoryByVersion(version);
    }

    @Override
    public void saveHistoryBackup(String historyBackup, int version) {
        studentDao.saveHistoryBackup(historyBackup,version);
    }

    @Override
    public String findHistoryBackup(int version) {
        return studentDao.findHistoryBackup(version);
    }

    @Override
    public List<History> findHistoryList() {
        //number  inserttime    batch   version

        return studentDao.findHistoryList();
    }

    @Override
    public void deleteHistory(int version) {
        studentDao.deleteHistory(version);
    }

    @Override
    public void deleteStudentList() {
        studentDao.deleteStudentList();
    }
}
