package com.self_back.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Test {
    boolean[] study;
    Map<Integer,List<Integer>> map = new HashMap<>();
    boolean[] vis;
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        study = new boolean[numCourses+10];
        vis = new boolean[numCourses+10];
        int n = prerequisites.length;
        for(int[] p:prerequisites) {
            List<Integer> prereqs = map.getOrDefault(p[0], new ArrayList<>());
            prereqs.add(p[1]);
            map.put(p[0], prereqs);
        }
        for(int i=0;i<numCourses;i++) {
            if(study[i]) continue;
            if(!dfs(i)) return false;
        }
        return true;
    }
    public boolean dfs(int num) {
        vis[num] = true;
        if(study[num]) return true;
        else {
            if(map.get(num) != null) {
                for(int i=0;i<map.get(num).size();i++) {
                    boolean t = false;
                    if(vis[map.get(num).get(i)]) t = study[map.get(num).get(i)];
                    else t =  dfs(map.get(num).get(i));
                    if(!t) return false;
                }
                study[num] = true;
                return true;
            } else {
                study[num] = true;
                return true;
            }
        }
    }
    public static void main(String[] args) {
        Test test = new Test();
        int[][] prerequisites = {{1,4},{2,4},{3,1},{3,2}};
        System.out.println(test.canFinish(5,prerequisites));
    }

}