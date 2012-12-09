package models.comparators;

import models.Topic;

import java.util.Comparator;
import java.util.Date;

public class TopicComparator {

	public final static Comparator<Topic> TOPIC_DATE_COMPARATOR = new Comparator<Topic>() {

		public int compare(Topic t1, Topic t2) {
			Date date1 = t1.lastUpdateDate;
			Date date2 = t2.lastUpdateDate;

			if (date1 == null && date2 == null) {
				return 0;
			}

			if (date1 == null && date2 != null) {
				return -1;
			}

			if (date1 != null && date2 == null) {
				return 1;
			}

			if (date1.after(date2)) {
				return -1;
			}

			if (date2.after(date1)) {
				return 1;
			}

			return 0;
		}
	};
}