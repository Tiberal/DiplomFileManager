package com.oleg.diplomfilemanager.dialogs;

import com.oleg.diplomfilemanager.Constants;
import com.oleg.diplomfilemanager.FileInfoItem;
import com.oleg.diplomfilemanager.YandexDiskManagment;
import com.yandex.disk.client.ProgressListener;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;

public class YandexDiskDownloadDialog extends DialogFragment implements
		ProgressListener {

	private ProgressDialog dialog;
	private static ListFragment listFragment;
	private Bundle bundle;
	private boolean cancel = false;

	public static YandexDiskDownloadDialog getInstance(
			FileInfoItem fileInfoItem, ListFragment fragment) {
		listFragment = fragment;
		Bundle bundle = new Bundle();
		bundle.putParcelable("file_info_item", fileInfoItem);
		YandexDiskDownloadDialog downloadDialog = new YandexDiskDownloadDialog();
		downloadDialog.setArguments(bundle);
		return downloadDialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		bundle = new Bundle();
		bundle = getArguments();
		FileInfoItem fileInfoItem = bundle.getParcelable("file_info_item");
		dialog = new ProgressDialog(getActivity());
		dialog.setTitle("Загрузка");
		dialog.setMessage(fileInfoItem.getDisplayName());
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setIndeterminate(true);
		dialog.setButton(ProgressDialog.BUTTON_NEUTRAL, "Отмена",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						cancel = true;
					}
				});
		dialog.show();
		loadFile(fileInfoItem);
		return dialog;
	}

	private void setDownloadProgress(long loaded, long total) {
		if (dialog != null) {
			if (dialog.isIndeterminate()) {
				dialog.setIndeterminate(false);
			}
			if (total > Integer.MAX_VALUE) {
				dialog.setProgress((int) (loaded / Constants.PROGRESS));
				dialog.setMax((int) (total / Constants.PROGRESS));
			} else {
				dialog.setProgress((int) loaded);
				dialog.setMax((int) total);
			}
		}
	}

	private void downloadComplete() {
		dismiss();
	}

	private void loadFile(final FileInfoItem item) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				YandexDiskManagment.getInstance().downloadYandexDiskFile(item,
						YandexDiskDownloadDialog.this);
				YandexDiskDownloadDialog.this.downloadComplete();
			}

		}).start();
	}

	@Override
	public void updateProgress(final long loaded, final long total) {
		listFragment.getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				YandexDiskDownloadDialog.this
						.setDownloadProgress(loaded, total);
			}
		});
	}

	@Override
	public boolean hasCancelled() {
		return cancel;
	}

}
