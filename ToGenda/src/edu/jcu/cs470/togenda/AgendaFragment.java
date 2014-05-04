package edu.jcu.cs470.togenda;

import com.fima.cardsui.objects.CardStack;
import com.fima.cardsui.views.CardUI;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.text.Format; 
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import android.content.ContentUris;
import android.database.Cursor; 
import android.graphics.drawable.Drawable;
import android.provider.CalendarContract;
import android.text.format.DateFormat;
import android.text.format.Time;

@SuppressLint("SimpleDateFormat")
public class AgendaFragment extends Fragment{

	private View myFragmentView;
	private static final int MiliSecDay = 86400000;	//Number of Milliseconds in a day.
	private Cursor mCursor = null;
	//	private String whereClauseTrue = "CalendarContract.Events.ALL_DAY=1";
	//	private String whereClauseFalse = "CalendarContract.Events.ALL_DAY=0";

	//Contains all columns we are to recieve from Google Calendar.
	private static final String[] COLS = new String[]{ CalendarContract.Instances.EVENT_ID, 
		CalendarContract.Instances.TITLE,  CalendarContract.Events.DESCRIPTION, CalendarContract.Instances.START_DAY, 
		CalendarContract.Instances.BEGIN, CalendarContract.Instances.END, CalendarContract.Instances.END_MINUTE, 
		CalendarContract.Instances.EVENT_COLOR_KEY, CalendarContract.Events.CALENDAR_COLOR_KEY, CalendarContract.Instances.EVENT_COLOR, 
		CalendarContract.Events.ALL_DAY};

	public CardUI CardView;

	DBAdapter db;

	public Drawable backgroundColor;
	AlertDialog alertDialog;
	TaskCreator taskCreator;

	@SuppressWarnings("unchecked")
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for getActivity() fragment

		myFragmentView = inflater.inflate(R.layout.fragment_main, container, false);

		// init CardView
		CardView = (CardUI) myFragmentView.findViewById(R.id.cardsviewday);
		CardView.setSwipeable(false); //Global variable for now. Need to change library so we can set swipable on per-card basis.

		//Date formating
		Format df = DateFormat.getDateFormat(getActivity());
		Format tf = DateFormat.getTimeFormat(getActivity()); 
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
		Date d = new Date();

		//Date Card
		String datelabel = sdf.format(d)+", "+tf.format(d)+", "+df.format(d);

		//getting current time for use in query
		Time t = new Time();
		t.setToNow();
		db = new DBAdapter(getActivity());

		//Getting URI for calendar
		Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI.buildUpon();
		ContentUris.appendId(eventsUriBuilder, t.toMillis(true)); //start time = now
		ContentUris.appendId(eventsUriBuilder, t.toMillis(true)+(MiliSecDay*7));//End time = (now + 1 week)
		Uri eventsUri = eventsUriBuilder.build();

		//Fill cursor with desired calendar events.
		mCursor = getActivity().getContentResolver().query(eventsUri, COLS, null, null, CalendarContract.Instances.DTSTART + " ASC");
		mCursor.moveToFirst();

		ArrayList<CardTemplate> cardList = new ArrayList<CardTemplate>();

		boolean makeCards = true;

		CardStack stack = new CardStack();
		stack.setTitle(datelabel);
		stack.setColor("#33b5e5");

		while(makeCards)
		{		
			EventCard newCard = getEvent();
			if (newCard.AllDay == true)
			{
				if (newCard.startTime <= t.toMillis(true))
				{
					stack.add(newCard);
				}
			}
			else if (newCard.getTitle() != "no event") //"no event" == try-catch block
			{
				cardList.add(newCard);
			}
			else
			{
				makeCards = false; //do not add card
			}
			if (newCard.isLast() == true)
			{
				makeCards = false;
			}
		}

		Collections.sort(cardList); //works now

		//GET TASKS HERE
		db.open();
		//db.insertTask("finish project", "Seriously", 0, "1", 0);
		Cursor TaskCursor = db.getAllTasks();

		ArrayList<CardTemplate> TaskList = new ArrayList<CardTemplate>();

		try{

			if (TaskCursor != null)
			{
				makeCards = true;
			}
			while(makeCards)
			{
				TaskList.add(new TaskCard(TaskCursor.getInt(0),TaskCursor.getString(1), TaskCursor.getString(2), TaskCursor.getLong(3),
						TaskCursor.getInt(4),TaskCursor.getInt(5), getActivity(), getFragmentManager(), TaskCursor.getInt(6)));
				if(TaskCursor.isLast()) 
				{
					makeCards = false;
				}
				else
				{
					TaskCursor.moveToNext();
				}
			}
		}
		catch(Exception E)
		{
		}
		db.close();


		//SORT TASKS + EVENTS TOGETHER HERE
		Collections.sort(TaskList);

		//ArrayList<CardTemplate> JoinedList = new ArrayList<CardTemplate>();

		CardView.addStack(stack);

		int eventLength = cardList.size();
		int taskLength = TaskList.size();
		Long blockStart = t.toMillis(true);
		Long MiliHalfHour = (long) 1800000;
		while (eventLength > 0)
		{
			//Stacked cards are kind of awkward to use, and when placed in excession they cause lag.
			//Ordinary events will no longer be stacked. Instead, only full-day events will be stacked with each other when
			//multiple full-day events exist on the same day.
			//full day events aren't properly implemented yet.
			//Will use a different card format for full day events, as well as tasks so that different types of entries are
			//easily identified.

			if(taskLength > 0)
			{
				if (cardList.get(eventLength-1).getStart()-blockStart >= (MiliHalfHour*taskLength))
				{
					CardView.addCard(TaskList.get(taskLength-1));
					blockStart += MiliHalfHour*taskLength;
					taskLength-=1;
				}
				else
				{
					CardView.addCard(cardList.get(eventLength-1));
					blockStart = cardList.get(eventLength-1).getEnd();
					eventLength-=1;
				}
			}
			else
			{
				CardView.addCard(cardList.get(eventLength-1));
				blockStart = cardList.get(eventLength-1).getEnd();
				eventLength-=1;
			}
		}
		while(taskLength>0)
		{
			CardView.addCard(TaskList.get(taskLength-1));
			taskLength-=1;
		}

		if (cardList.isEmpty())
		{
			//Create cardtype that explains that there are no current events.
		}

		// draw cards
		CardView.refresh();

		return myFragmentView;
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}


	public EventCard getEvent() {
		try
		{
			EventCard event;
			String title;
			long start;
			long end;
			//			int color; //holds custom color case
			String colorKey, colorKey2;
			String desc;
			String eventId;
			boolean last = false;
			boolean allday = false;

			try {

				//				CalendarContract.Instances.EVENT_ID,
				eventId = mCursor.getString(0);
				//				CalendarContract.Instances.TITLE,
				title = mCursor.getString(1);
				//				CalendarContract.Events.DESCRIPTION, 
				desc = mCursor.getString(2);
				//				CalendarContract.Instances.START_DAY,

				//				CalendarContract.Instances.START_MINUTE,
				start = mCursor.getLong(4);					//MIGHT REQUIRE LONG
				//				CalendarContract.Instances.END_DAY,

				//				CalendarContract.Instances.END_MINUTE,
				end = mCursor.getLong(5);					//MIGHT REQUIRE LONG
				//				CalendarContract.Instances.EVENT_COLOR_KEY,
				colorKey = mCursor.getString(7);
				colorKey2 = mCursor.getString(8);
				//				CalendarContract.Instances.EVENT_COLOR};

				if (mCursor.getInt(10) == 1)
				{
					allday = true;
				}

			} catch (Exception e) {
				//ignore
				return new EventCard("no event");
			}

			if(mCursor.isLast()) 
			{
				last = true;
			}
			else
			{
				mCursor.moveToNext();
			}

			//creates event (title,description,star time,end time,event color, calendar color,is clickable,have overflow button,id,is last)
			event = new EventCard(title, desc, start, end, colorKey, colorKey2, true, true, eventId, last, allday);

			return event;
		}
		catch(Exception ex)
		{
			return new EventCard("no event");
		}
	}
}
