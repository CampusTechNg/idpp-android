package com.campustechng.aminu.idpenrollment.sourceafis.extraction.model;

import com.campustechng.aminu.idpenrollment.sourceafis.general.Calc;
import com.campustechng.aminu.idpenrollment.sourceafis.general.CircularArray;
import com.campustechng.aminu.idpenrollment.sourceafis.general.Point;
import com.campustechng.aminu.idpenrollment.sourceafis.general.ReversedList;

import java.util.List;

public final class Ridge {
	List<Point> Points;

	public List<Point> getPoints() {
		return Points;
	}

	SkeletonBuilderMinutia StartMinutia;
	SkeletonBuilderMinutia EndMinutia;

	Ridge Reversed;

	public Ridge getReversed() {
		return Reversed;
	}

	public void setReversed(Ridge value) {
		Reversed = value;
	}

	public SkeletonBuilderMinutia getStart() {
		return StartMinutia;
	}

	public void setStart(SkeletonBuilderMinutia value) {
		if (StartMinutia != value) {
			if (StartMinutia != null) {
				SkeletonBuilderMinutia detachFrom = StartMinutia;
				StartMinutia = null;
				detachFrom.DetachStart(this);
			}
			StartMinutia = value;
			if (StartMinutia != null)
				StartMinutia.AttachStart(this);
			Reversed.EndMinutia = value;
		}
	}

	public SkeletonBuilderMinutia getEnd() {
		return EndMinutia;
	}

	public void setEnd(SkeletonBuilderMinutia value) {
		if (EndMinutia != value) {
			EndMinutia = value;
			Reversed.setStart(value);
		}
	}

	public Ridge() {
		Points = new CircularArray<>();
		Reversed = new Ridge(this);
	}

	Ridge(Ridge reversed) {
		Reversed = reversed;
		Points = new ReversedList<>(reversed.Points);
	}

	public void setContent(Ridge other) {
		/*this.setStart(other.StartMinutia);
		this.setEnd(other.EndMinutia);
		this.Points = other.Points;
		this.Reversed = other.Reversed;*/
		this.StartMinutia = other.StartMinutia;
		this.EndMinutia = other.EndMinutia;
		this.Points = other.Points;
		this.Reversed = other.Reversed;
		
	}

	public void Detach() {
		setStart(null);
		setEnd(null);
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof Ridge)) {
			return false;
		}
		Ridge ridge = (Ridge) other;
		return Calc.areEqual(this.StartMinutia, ridge.StartMinutia) && Calc.areEqual(this.EndMinutia, ridge.EndMinutia);
	}
}