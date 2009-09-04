package net.woft.handwriting;

import net.woft.handwriting.R.id;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HandwritingActivity extends Activity {
	
	FingerDrawView view;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		view = (FingerDrawView)findViewById(id.draw_view);
		
		Button button;
		button = (Button)findViewById(id.reset_button);
		button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				view.resetView();
			}
		});
	}
}