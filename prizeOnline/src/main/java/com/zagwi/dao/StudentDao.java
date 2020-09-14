package com.zagwi.dao;

import com.zagwi.domain.History;
import com.zagwi.domain.Student;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface StudentDao {

    @Insert("insert into student values(null,#{name},#{remark},#{number})")
    void add(Student student);

    @Select("select * from student")
    List<Student> findAll();

    @Select("select number from student")
    List<String> findAllNumbers();

    @Select("select * from whitelist")
    List<Student> findWhitelist();

    @Select("select * from student where name not in (select name from blacklist union select name from whitelist)")
    List<Student> findUndetermined();

    @Insert("insert into history values(null,#{name},#{version},#{number},null,#{batch},#{isdelete})")
    void insertHistory(History history);

    @Select("select ifnull(max(version),0) from history")
    int getMaxVersion();

    @Select("select h.id,(select name from student s where s.number=h.number) name ,h.inserttime,h.version,h.batch,h.isdelete,h.number from history h where version = #{version} and  isdelete=0 ")
    List<History> findHistoryByVersion(int version);

    @Update("<script>"
            + "update history set batch = #{batch} where version = #{version} and number in "
            + "<foreach item='item' index='index' collection='numbers' open='(' separator=',' close=')'>"
            +       "#{item}"
            + "</foreach>"
              + "</script>")
    void updateHistory(@Param("version") int version, @Param("numbers") String[] pickedNumbers, @Param("batch") int batch);

    @Insert("<script>"
            + "insert into history values  "
            + "<foreach collection='historyList' item='item' index='index' separator=',' >"
            +       "(null,#{item.name},#{item.version},#{item.number},null,#{item.batch},#{item.isdelete})"
            + "</foreach>"
            + "</script>")
    void insertHistoryBatchMode(@Param("historyList") List<History> historiesForInsert);

    @Select("select h.id,(select name from student s where s.number=h.number) name ,h.inserttime,h.version,h.batch,h.isdelete,h.number  from history h where version = #{version} and  isdelete=0  and batch > 0 order by batch")
    List<History> findPickedHistoryByVersion(int version);

    @Insert("<script>"
            + "insert into student values  "
            + "<foreach collection='studentList' item='item' index='index' separator=',' >"
            +       "(null,#{item.name},#{item.remark},#{item.number})"
            + "</foreach>"
            + "</script>")
    void addBatch(@Param("studentList")List<Student> data);

    @Insert("insert into historybackup values(null,#{data},#{version},null)")
    void saveHistoryBackup(@Param("data") String historyBackup, @Param("version") int version);

    @Select("select data from historybackup where version = #{version} order by inserttime desc limit 1")
    String findHistoryBackup(int version);

    @Select("select h.id," +
            "(select name from student s where s.number=h.number) name ," +
            "h.inserttime,h.version,max(batch) batch,h.isdelete,h.number" +
            " from history h where h.`batch` > 0 and isdelete=0 " +
            " group by version  order by inserttime desc")
    List<History> findHistoryList();

    @Update("update history set isdelete = 1 where version = #{version}")
    void deleteHistory(int version);

    @Delete("delete from student")
    void deleteStudentList();
}
