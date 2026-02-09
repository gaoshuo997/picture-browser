package com.jimmy.tools;

import com.jimmy.entity.Statement;
import com.jimmy.repository.SentenceRepository;
import jakarta.annotation.Resource;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StatementTools {

    @Resource
    private SentenceRepository sentenceRepository;

    @Tool(description = "保存用户要学习的语言句子")
    public String saveStatement(@ToolParam(description = "生成的多条语言句子") List<Statement> sentenceRecordList){
//        List<Statement> statementList = new ArrayList<>(sentenceRecordList.size());
//        Date now = new Date();
//        for (SentenceRecord sentenceRecord : sentenceRecordList){
//            Statement statement = new Statement();
//            statement.setEnglish(sentenceRecord.english());
//            statement.setChinese(sentenceRecord.chinese());
//            statement.setOrder(sentenceRecord.order());
//            statement.setCreatedAt(now);
//            statement.setUpdatedAt(now);
//            statementList.add(statement);
//        }
        sentenceRepository.saveAll(sentenceRecordList);
        return "句子保存成功";
    }
}
