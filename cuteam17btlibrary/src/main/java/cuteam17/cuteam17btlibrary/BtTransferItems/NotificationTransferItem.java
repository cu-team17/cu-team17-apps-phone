package cuteam17.cuteam17btlibrary.BtTransferItems;

import android.app.Notification;
import android.app.NotificationManager;
import android.graphics.drawable.Icon;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class NotificationTransferItem extends BtTransferItem {

	private String applicationLabel;
	private String title;
	private String titleBig;
	private String text;
	private String bigText;
	private String summaryText;
	private boolean showWhen = false;
	private long when;
	//private Icon largeIcon;
	//private Icon smallIcon;

	//private Notification.Style style;

	public NotificationTransferItem() {
		super(TransferItemType.NOTIFICATION);
	}

	public void setApplicationLabel(String applicationLabel) {
		this.applicationLabel = applicationLabel;
	}

	public String getApplicationLabel() {
		return applicationLabel;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setTitleBig(String titleBig) {
		this.titleBig = titleBig;
	}

	public String getTitleBig() {
		return titleBig;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setBigText(String bigText) {
		this.bigText = bigText;
	}

	public String getBigText() {
		return bigText;
	}

	public void setSummaryText(String summaryText) {
		this.summaryText = summaryText;
	}

	public String getSummaryText() {
		return summaryText;
	}

	public void setShowWhen(Boolean showWhen) {
		this.showWhen = showWhen;
	}

	public boolean getShowWhen() {
		return showWhen;
	}

	public void setWhen(long when) {
		this.when = when;
	}

	public long getWhen() {
		return when;
	}

	/*
	public void setLargeIcon(Icon largeIcon) {
		this.largeIcon = largeIcon;
	}

	public Icon getLargeIcon() {
		return largeIcon;
	}

	public void setSmallIcon(Icon smallIcon) {
		this.smallIcon = smallIcon;
	}

	public Icon getSmallIcon() {
		return smallIcon;
	}
	*/





}
