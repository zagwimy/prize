package com.zagwi.service;

import com.zagwi.domain.History;
import com.zagwi.domain.RollResult;
import com.zagwi.domain.Student;

import java.util.List;
import java.util.Map;

public interface StudentService {

    List<Student> add(List<Student> data);

    List<Student> findAll();

    RollResult getRollResult();

    void pickStudents(int version, String[] pickedNumbers, int batch);

    List<History> findHistoryByVersion(int version);

    void saveHistoryBackup(String historyBackup, int version);

    String findHistoryBackup(int version);

    List<History> findHistoryList();

    void deleteHistory(int version);

    void deleteStudentList();
}
