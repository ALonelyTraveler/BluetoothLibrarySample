package com.bdkj.ble.spp;

import com.bdkj.ble.link.ConnectCallBack;

/**
 * 只写不读的线程 Created by chenwei on 15/10/16.
 */
public class WriteThread extends BaseThread {

	public ConnectCallBack handler;

	SppService service;

	byte[] data;

	private boolean isCancel = false;

	public WriteThread(ConnectCallBack handler, SppService service, byte[] data) {
		this.handler = handler;
		this.service = service;
		this.data = data;
	}

	@Override
	public void run() {
		super.run();
		boolean complete = false;
		try {
			Thread.sleep(200);
			service.write(data);
			service.getOutputStream().flush();
			complete = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!isCancel) {
			if (!complete) {
				handler.connectFail();
			}
		}
		handler = null;
		service = null;
	}

	@Override
	public void cancel() {
		interrupt();
		isCancel = true;
	}

	@Override
	public boolean isCancel() {
		return isCancel;
	}
}
