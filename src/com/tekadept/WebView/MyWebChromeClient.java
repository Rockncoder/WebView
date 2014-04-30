package com.tekadept.WebView;

import android.app.Activity;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyWebChromeClient extends WebChromeClient {
	Activity myActivity;
	RelativeLayout childLayout;
	RelativeLayout browserLayout;
	private TextView titleText;


	public MyWebChromeClient(Activity activity, RelativeLayout wrappingLayout, RelativeLayout browserLayout, TextView titleText) {
		myActivity = activity;
		this.childLayout = wrappingLayout;
		this.browserLayout = browserLayout;
		this.titleText = titleText;
	}

	@Override
	public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
		// remove any current child views
		browserLayout.removeAllViews();
		// make the child web view's layout visible
		childLayout.setVisibility(View.VISIBLE);

		// now create a new web view
		WebView newView = new WebView(myActivity);
		WebSettings settings = newView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setSupportMultipleWindows(true);
		settings.setUseWideViewPort(false);
		newView.setWebViewClient(new WebViewClient() {
			/**
			 * Need to grab the title of the web page
			 * @param view - - the web view
			 * @param url - the URL of the page
			 */
			public void onPageFinished(WebView view, String url) {
				// once the view has loaded, display its title
				titleText.setText(view.getTitle());
			}
		});
		// add the new web view to the layout
		newView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		browserLayout.addView(newView);
		// tell the transport about the new view
		WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
		transport.setWebView(newView);
		resultMsg.sendToTarget();

		// let's be cool and slide the new web view up into view
		Animation slideUp = AnimationUtils.loadAnimation(myActivity, R.anim.slide_up);
		childLayout.startAnimation(slideUp);
		return true;
	}

	/**
	 * Lower the child web view down
	 */
	public void closeChild() {
		Log.v(Constants.LOG_TAG, "Closing Child WebView");
		Animation slideDown = AnimationUtils.loadAnimation(myActivity, R.anim.slide_down);
		childLayout.startAnimation(slideDown);
		slideDown.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				titleText.setText("");
				childLayout.setVisibility(View.INVISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
	}

	public boolean isChildOpen() {
		return childLayout.getVisibility() == View.VISIBLE;
	}
}
