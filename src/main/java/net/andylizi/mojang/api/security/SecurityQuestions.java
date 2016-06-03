/*
 * Copyright (C) 2016 andylizi.
 * 
 * This library is free software: you can redistribute it and/or  
 * modify it under the terms of the GNU Lesser General Public     
 * License as published by the Free Software Foundation, either   
 * version 3 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  
 * GNU Lesser General Public License for more details.            
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.  If not, see 
 * <http://www.gnu.org/licenses/>.
 */
package net.andylizi.mojang.api.security;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import net.andylizi.mojang.api.security.SecurityQuestions.Question.QuestionAdapter;

/**
 * 验证账户所需的安全问题. 
 * @author andylizi
 */
public class SecurityQuestions implements Serializable{
    protected static final QuestionAdapter QUESTION_ADAPTER = new QuestionAdapter();

    private Question[] questions;
    
    private SecurityQuestions(){};

    /**
     * 从数组构造一组安全问题
     * @param questions 存放安全问题的数组
     */
    public SecurityQuestions(Question... questions) {
        this.questions = questions;
    }

    /**
     * 将JSON转换为安全问题. 
     * @param json JSON
     */
    public SecurityQuestions(String json) {
        try {
            List<Question> list = new ArrayList<>(3);
            try (JsonReader reader = new JsonReader(new StringReader(json))) {
                reader.beginArray();
                while(reader.hasNext())
                    list.add(QUESTION_ADAPTER.read(reader));
                reader.endArray();
            }
            questions = list.toArray(new Question[0]);
        } catch (IOException ex) {
            throw new AssertionError("are you kidding me? is StringWriter!");
        }
    }

    /**
     * 得到问题列表.
     * @return 安全问题列表
     */
    public Question[] getQuestions() {
        return questions.clone();
    }
    
    /**
     * 得到指定序号的问题 <b>从0开始</b>.
     * @param index 从0开始计算的序号
     * @return 安全问题列表
     */
    public Question getQuestion(int index) {
        return questions[index];
    }
    
    /**
     * 设置指定序号的安全问题的回答
     * @param i 序号
     * @param answer 答案
     * @throws ArrayIndexOutOfBoundsException 下标越界
     */
    public void answer(int i, String answer) throws ArrayIndexOutOfBoundsException{
        questions[i].answer(answer);
    }
    
    /**
     * 设置第一个安全问题的回答
     * @param answer 答案
     * @throws ArrayIndexOutOfBoundsException 第一个问题不存在
     */
    public void answerFirst(String answer) throws ArrayIndexOutOfBoundsException{
        answer(0, answer);
    }
    
    /**
     * 设置第二个安全问题的回答
     * @param answer 答案
     * @throws ArrayIndexOutOfBoundsException 第二个问题不存在
     */
    public void answerSecond(String answer) throws ArrayIndexOutOfBoundsException{
        answer(1, answer);
    }
    
    /**
     * 设置第三个安全问题的回答
     * @param answer 答案
     * @throws ArrayIndexOutOfBoundsException 第三个问题不存在
     */
    public void answerThird(String answer) throws ArrayIndexOutOfBoundsException{
        answer(2, answer);
    }
    
    /**
     * 问题是否全部填写完成. 
     * @return 是否全部填写完成
     */
    public boolean isComplete(){
        for(Question question : questions){
            if(!question.isComeplete())
                return false;
        }
        return true;
    }
    
    public String toJSON(){
        StringWriter sw = new StringWriter();
        try (JsonWriter writer = new JsonWriter(sw)) {
            writer.beginArray();
            for(Question question : questions)
                QUESTION_ADAPTER.write(writer, question);
            writer.endArray();
        } catch (IOException ex) {
            throw new AssertionError("are you kidding me? is StringWriter!");
        }
        return sw.toString();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        for(int i = 0; i < questions.length; i++){
            builder.append(questions[i].toString());
            if(i != questions.length - 1)
                builder.append(',');
        }
        return builder.append(']').toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) 
            return true;
        if (obj == null) 
            return false;
        if (getClass() != obj.getClass()) 
            return false;
        return Arrays.deepEquals(this.questions, ((SecurityQuestions) obj).questions);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Arrays.deepHashCode(this.questions);
        return hash;
    }

    /**
     * 一个问题.
     */
    public static class Question implements Serializable, Cloneable{
        /**
         * 问题ID
         */
        public final int questionID;
        
        /**
         * 问题内容
         */
        public final String question;
        
        /**
         * 答案ID
         */
        public final int answerID;
        
        /**
         * 答案内容
         */
        private String answer;

        public Question(int questionID, String question, int answerID) {
            this.questionID = questionID;
            this.question = Objects.requireNonNull(question);
            this.answerID = answerID;
        }

        /**
         * 回答问题
         * @param answer 答案
         */
        public void answer(String answer) {
            this.answer = answer;
        }
        
        /**
         * 判断问题是否回答完毕
         * @return 问题是否回答完毕
         */
        public boolean isComeplete(){
            return answer != null && !answer.isEmpty();
        }

        @Override
        public String toString() {
            return new StringBuilder().append('{')
                    .append("questionID:").append(questionID)
                    .append(",question:").append(question)
                    .append(",answerID:").append(answerID)
                    .append('}').toString();
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            Question clone = new Question(questionID, question, answerID);
            if(isComeplete())
                clone.answer(answer);
            return clone;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) 
                return true;
            if (obj == null) 
                return false;
            if (getClass() != obj.getClass()) 
                return false;
            final Question other = (Question) obj;
            if (this.questionID != other.questionID) 
                return false;
            if (this.answerID != other.answerID) 
                return false;
            return Objects.equals(this.question, other.question);
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 89 * hash + this.questionID;
            hash = 89 * hash + Objects.hashCode(this.question);
            hash = 89 * hash + this.answerID;
            return hash;
        }
        
        public static class QuestionAdapter extends TypeAdapter<Question>{
            @Override
            public void write(JsonWriter writer, Question obj) throws IOException {
                writer
                    .beginObject()
                        .name("id").value(obj.answerID)
                        .name("answer").value(obj.answer)
                    .endObject();
            }

            @Override
            public Question read(JsonReader reader) throws IOException {
                reader.beginObject();
                int questionID = -1; String question = "undefined"; int answerID = -1; String answer = null;
                while(reader.hasNext()){
                    switch(reader.nextName()){
                        case "question":
                            reader.beginObject();
                            while(reader.hasNext()){
                                switch(reader.nextName()){
                                    case "id":
                                        questionID = reader.nextInt();
                                        break;
                                    case "question":
                                        question = reader.nextString();
                                        break;
                                }
                            }
                            reader.endObject();
                            break;
                        case "answer":
                            reader.beginObject();
                            while(reader.hasNext()){
                                switch(reader.nextName()){
                                    case "id":
                                        answerID = reader.nextInt();
                                        break;
                                    case "answer":
                                        answer = reader.nextString();
                                        break;
                                }
                            }
                            reader.endObject();
                            break;
                    }
                }
                reader.endObject();
                return new Question(questionID, question, answerID);
            }
        }
    }
}
