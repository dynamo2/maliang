package com.maliang.core.ui.controller;

public class Pager {
	public static int PAGE_SIZE = 5;
	private int curPage = 1; // 当前页
    private int pageSize = PAGE_SIZE; // 每页多少行
    private int totalRow; // 共多少行
    private int start;// 当前页起始行
    private int end;// 结束行
    private int totalPage = -1; // 共多少页

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
    
    public void setCurPage(Object page) {
    	if(page instanceof String){
			try {
				page = Integer.valueOf((String)page);
				setCurPage((int)page);
				return;
			}catch(Exception e){}
		}
    	setCurPage(1);
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
    
    public void setPageSize(Object size) {
    	if(size instanceof String){
			try {
				size = Integer.valueOf((String)size);
				setPageSize((int)size);
				return;
			}catch(Exception e){}
		}
    	setPageSize(PAGE_SIZE);
    }

    public int getTotalRow() {
        return totalRow;
    }

    public void setTotalRow(int totalRow) {
        this.totalRow = totalRow;
        this.reDo();
    }

    public int getTotalPage() {
        return this.totalPage;
    }
    
    private void reDo(){
    	totalPage = (totalRow + pageSize - 1) / pageSize;
        if (totalPage < curPage) {
            curPage = totalPage;
            start = pageSize * (curPage - 1);
            end = totalRow;
        }
        end = start + pageSize > totalRow ? totalRow : start + pageSize;
    }
}
