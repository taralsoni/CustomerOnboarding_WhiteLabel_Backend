package com.afrAsia.entities.request;

import java.io.Serializable;

public class ApplicationDetailsReq implements Serializable {
	private ApplicationDetailsReq.Data data;

	public ApplicationDetailsReq.Data getData() {
		return data;
	}

	public void setData(ApplicationDetailsReq.Data data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "ClassPojo [data = " + data + "]";
	}
	
	public static class Data implements Serializable {
		
		private static final long serialVersionUID = -6145838904264249027L;

		private Integer refNo;
		
		public Integer getRefNo() {
			return refNo;
		}

		public void setRefNo(Integer refNo) {
			this.refNo = refNo;
		}

		
		@Override
		public String toString() {
			return "Data [refNo=" + refNo + "]";
		}
		
	}
		
}
