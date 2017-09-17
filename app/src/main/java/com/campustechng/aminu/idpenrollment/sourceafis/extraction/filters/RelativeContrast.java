/**
 * @author Veaceslav Dubenco
 * @since 18.10.2012
 */
package com.campustechng.aminu.idpenrollment.sourceafis.extraction.filters;

import com.campustechng.aminu.idpenrollment.sourceafis.general.BinaryMap;
import com.campustechng.aminu.idpenrollment.sourceafis.general.BlockMap;
import com.campustechng.aminu.idpenrollment.sourceafis.general.Calc;
import com.campustechng.aminu.idpenrollment.sourceafis.general.DetailLogger;
import com.campustechng.aminu.idpenrollment.sourceafis.meta.DpiAdjusted;
import com.campustechng.aminu.idpenrollment.sourceafis.meta.Parameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 */
public final class RelativeContrast {
	@DpiAdjusted
	@Parameter(lower = 10 * 10, upper = 2000 * 2000)
	public int SampleSize = 168568;
	@Parameter
	public float SampleFraction = 0.49f;
	@Parameter
	public float RelativeLimit = 0.34f;

	public DetailLogger.Hook Logger = DetailLogger.off;

	public BinaryMap DetectLowContrast(byte[][] contrast, BlockMap blocks) {
		List<Integer> sortedContrast = new ArrayList<Integer>();
		for (byte[] contrastItemLine : contrast)
			for (byte contrastItem : contrastItemLine)
				sortedContrast.add(contrastItem & 0xFF);
		// sortedContrast.Sort();
		// sortedContrast.Reverse();
		Collections.sort(sortedContrast, Collections.reverseOrder());

		int pixelsPerBlock = Calc.GetArea(blocks.getPixelCount())
				/ blocks.getAllBlocks().getTotalArea();
		int sampleCount = Math.min(sortedContrast.size(), SampleSize
				/ pixelsPerBlock);
		int consideredBlocks = Math.max(
				Calc.toInt32(sampleCount * SampleFraction), 1);

		int averageContrast = 0;
		for (int i = 0; i < consideredBlocks; ++i)
			averageContrast += sortedContrast.get(i);
		averageContrast /= consideredBlocks;
		int limit = Calc.toByte(averageContrast * RelativeLimit) & 0xFF;

		BinaryMap result = new BinaryMap(blocks.getBlockCount().Width,
				blocks.getBlockCount().Height);
		for (int y = 0; y < result.getHeight(); ++y)
			for (int x = 0; x < result.getWidth(); ++x)
				if ((contrast[y][x] & 0xFF) < limit)
					result.SetBitOne(x, y);
		Logger.log(result);
		return result;
	}
}
