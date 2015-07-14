package com.maliang.core.ui.controller;

public class Pager {
	public static int PAGE_SIZE = 5;
	private int curPage = 1; // 当前页
    private int pageSize = PAGE_SIZE; // 每页多少行
    private int totalRow; // 共多少行
    private int start;// 当前页起始行
    private int end;// 结束行
    private int totalPage; // 共多少页

    public int getCurPage() {
        return curPage;
    }

    public void setCurPage(int curPage) {
        if (curPage < 1) {
            curPage = 1;
        } else {
            start = pageSize * (curPage - 1);
        }
        
        end = start + pageSize > totalRow ? totalRow : start + pageSize;
        this.curPage = curPage;
    }

    public int getStart() {
        return start;
    }
    
    public void setStart(int st){
    	this.start = st;
    	
    	this.curPage = this.start/this.pageSize+1;
    }

    public int getEnd() {

        return end;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int size) {
    	if(size < 1){
    		size = PAGE_SIZE;
    	}
        this.pageSize = size;
    }

    public int getTotalRow() {
        return totalRow;
    }

    public void setTotalRow(int totalRow) {
        totalPage = (totalRow + pageSize - 1) / pageSize;
        this.totalRow = totalRow;
        if (totalPage < curPage) {
            curPage = totalPage;
            start = pageSize * (curPage - 1);
            end = totalRow;
        }
        end = start + pageSize > totalRow ? totalRow : start + pageSize;
    }

    public int getTotalPage() {
        return this.totalPage;
    }
}
