package com.example.mayliu.opencvplate;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

/**
 * Created by mayliu on 2018/4/3.
 */

public class findMatchingChars {

    public ArrayList<ArrayList<PossibleChar>> findVectorOfVectorsOfMatchingChars(ArrayList<PossibleChar> ListofPossibleChars) {
        //https://www.tangiblesoftwaresolutions.com/articles/java_equivalent_to_cplus_vector.html
        //返回值
        ArrayList<ArrayList<PossibleChar>> vectorOfVectorsOfMatchingChars=new ArrayList<ArrayList<PossibleChar>>();

        int MIN_NUMBER_OF_MATCHING_CHARS =6;
//       int MAX_NUMBER_OF_MATCHING_CHARS =10;

        // 对于第一部筛选后的countor集合
        for (PossibleChar possibleChar : ListofPossibleChars) {

            // find all chars in the big vector that match the current char
            ArrayList<PossibleChar> vectorOfMatchingChars = findVectorOfMatchingChars(possibleChar, ListofPossibleChars);
            // also add the current char to current possible vector of matching chars
            vectorOfMatchingChars.add(possibleChar);

            //删除 小于group5的所有countor
            if (vectorOfMatchingChars.size() < MIN_NUMBER_OF_MATCHING_CHARS) {
                continue;
            }
//            if (vectorOfMatchingChars.size() >MAX_NUMBER_OF_MATCHING_CHARS) {
//                continue;
//            }
            // 加入大于group5的所有countor
            vectorOfVectorsOfMatchingChars.add(vectorOfMatchingChars);
            System.out.println("每个轮廓集中轮廓的个数："+vectorOfMatchingChars.size());

            // remove the current vector of matching chars from the big vector so we don't use those same chars twice,
            // make sure to make a new big vector for this since we don't want to change the original big vector

            ArrayList<PossibleChar> vectorOfPossibleCharsWithCurrentMatchesRemoved=new ArrayList<PossibleChar>();

            for (PossibleChar possChar : ListofPossibleChars) {
                boolean adding=true;
                for(PossibleChar possCharFind : vectorOfMatchingChars){
                    if(possCharFind== possChar){
                        adding=false;
                        break;
                    }
                }
                if(adding){
                    vectorOfPossibleCharsWithCurrentMatchesRemoved.add(possChar);
                }

            }
            // declare new vector of vectors of chars to get result from recursive call
            ArrayList<ArrayList<PossibleChar>> recursiveVectorOfVectorsOfMatchingChars=new ArrayList<>();
            // recursive call
            recursiveVectorOfVectorsOfMatchingChars = findVectorOfVectorsOfMatchingChars(vectorOfPossibleCharsWithCurrentMatchesRemoved);	// recursive call !!
            for (ArrayList<PossibleChar> recursiveVectorOfMatchingChars : recursiveVectorOfVectorsOfMatchingChars) {
                // for each vector of matching chars found by recursive call
                vectorOfVectorsOfMatchingChars.add(recursiveVectorOfMatchingChars);
                // add to our original vector of vectors of matching chars
            }

            break;

        }
        return(vectorOfVectorsOfMatchingChars);
    }



    public ArrayList<PossibleChar> findVectorOfMatchingChars(PossibleChar possibleChar,List<PossibleChar> vectorOfChars){
        // double MIN_DIAG_SIZE_MULTIPLE_AWAY = 0.3;
        int counts=0;
        double MAX_DIAG_SIZE_MULTIPLE_AWAY = 4.0;
        double MAX_CHANGE_IN_AREA = 0.5;
        double MAX_CHANGE_IN_WIDTH = 0.8;
        double MAX_CHANGE_IN_HEIGHT = 0.2;
        double MAX_ANGLE_BETWEEN_CHARS = 12.0;

        ArrayList<PossibleChar> vectorOfMatchingChars=new ArrayList<>();

        //vectorOfMatchingChars.add(possibleChar);//新添加的语句

        for (PossibleChar possibleMatchingChar : vectorOfChars) {// for each char in big vector
            if (possibleMatchingChar == possibleChar) {
                // then we should not include it in the vector of matches b/c that would end up double including the current char
                continue;           // so do not add to vector of matches and jump back to top of for loop
            }
            // compute stuff to see if chars are a match
            //计算两个char间的距离
            Calculate ca=new Calculate();
            double dblDistanceBetweenChars = ca.distanceBetweenChars(possibleChar, possibleMatchingChar);
            // System.out.println("dblDistanceBetweenChars"+dblDistanceBetweenChars);
            //计算两个char间的角度
            double dblAngleBetweenChars = ca.angleBetweenChars(possibleChar, possibleMatchingChar);
            // System.out.println("dblAngleBetweenChars"+dblAngleBetweenChars);
            //计算面积，高，宽间的差距
            double dblChangeInArea = (double) abs(possibleMatchingChar.boundingRect.area() - possibleChar.boundingRect.area()) / (double) possibleChar.boundingRect.area();
            // System.out.println("dblChangeInArea"+dblChangeInArea);
            double dblChangeInWidth = (double) abs(possibleMatchingChar.boundingRect.width - possibleChar.boundingRect.width) / (double) possibleChar.boundingRect.width;
            // System.out.println("dblChangeInWidth"+dblChangeInWidth);
            double dblChangeInHeight = (double) abs(possibleMatchingChar.boundingRect.height - possibleChar.boundingRect.height) / (double) possibleChar.boundingRect.height;
            // System.out.println("dblChangeInHeight"+dblChangeInWidth);
            if (dblDistanceBetweenChars < (possibleChar.dblDiagonalSize * MAX_DIAG_SIZE_MULTIPLE_AWAY) &&
                    dblAngleBetweenChars < MAX_ANGLE_BETWEEN_CHARS &&
                    dblChangeInArea < MAX_CHANGE_IN_AREA &&
                    dblChangeInWidth < MAX_CHANGE_IN_WIDTH &&
                    dblChangeInHeight < MAX_CHANGE_IN_HEIGHT) {
                vectorOfMatchingChars.add(possibleMatchingChar);
                counts++;// if the chars are a match, add the current char to vector of matching chars
            }
        }

        return(vectorOfMatchingChars);

    }
}
