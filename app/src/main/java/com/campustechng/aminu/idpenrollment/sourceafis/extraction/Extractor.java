/**
 * @author Veaceslav Dubenco
 * @since 08.10.2012
 */
package com.campustechng.aminu.idpenrollment.sourceafis.extraction;

import com.campustechng.aminu.idpenrollment.sourceafis.extraction.filters.CrossRemover;
import com.campustechng.aminu.idpenrollment.sourceafis.extraction.filters.HillOrientation;
import com.campustechng.aminu.idpenrollment.sourceafis.extraction.filters.ImageInverter;
import com.campustechng.aminu.idpenrollment.sourceafis.extraction.filters.InnerMask;
import com.campustechng.aminu.idpenrollment.sourceafis.extraction.filters.LocalHistogram;
import com.campustechng.aminu.idpenrollment.sourceafis.extraction.filters.OrientedSmoother;
import com.campustechng.aminu.idpenrollment.sourceafis.extraction.filters.SegmentationMask;
import com.campustechng.aminu.idpenrollment.sourceafis.extraction.filters.Thinner;
import com.campustechng.aminu.idpenrollment.sourceafis.extraction.filters.ThresholdBinarizer;
import com.campustechng.aminu.idpenrollment.sourceafis.extraction.filters.VotingFilter;
import com.campustechng.aminu.idpenrollment.sourceafis.extraction.minutiae.MinutiaCloudRemover;
import com.campustechng.aminu.idpenrollment.sourceafis.extraction.minutiae.MinutiaCollector;
import com.campustechng.aminu.idpenrollment.sourceafis.extraction.minutiae.MinutiaMask;
import com.campustechng.aminu.idpenrollment.sourceafis.extraction.minutiae.MinutiaShuffler;
import com.campustechng.aminu.idpenrollment.sourceafis.extraction.minutiae.StandardDpiScaling;
import com.campustechng.aminu.idpenrollment.sourceafis.extraction.minutiae.UniqueMinutiaSorter;
import com.campustechng.aminu.idpenrollment.sourceafis.extraction.model.BranchMinutiaRemover;
import com.campustechng.aminu.idpenrollment.sourceafis.extraction.model.DotRemover;
import com.campustechng.aminu.idpenrollment.sourceafis.extraction.model.FragmentRemover;
import com.campustechng.aminu.idpenrollment.sourceafis.extraction.model.GapRemover;
import com.campustechng.aminu.idpenrollment.sourceafis.extraction.model.PoreRemover;
import com.campustechng.aminu.idpenrollment.sourceafis.extraction.model.RidgeTracer;
import com.campustechng.aminu.idpenrollment.sourceafis.extraction.model.SkeletonBuilder;
import com.campustechng.aminu.idpenrollment.sourceafis.extraction.model.TailRemover;
import com.campustechng.aminu.idpenrollment.sourceafis.general.Angle;
import com.campustechng.aminu.idpenrollment.sourceafis.general.BinaryMap;
import com.campustechng.aminu.idpenrollment.sourceafis.general.BlockMap;
import com.campustechng.aminu.idpenrollment.sourceafis.general.DetailLogger;
import com.campustechng.aminu.idpenrollment.sourceafis.general.Size;
import com.campustechng.aminu.idpenrollment.sourceafis.meta.Action;
import com.campustechng.aminu.idpenrollment.sourceafis.meta.DpiAdjusted;
import com.campustechng.aminu.idpenrollment.sourceafis.meta.Nested;
import com.campustechng.aminu.idpenrollment.sourceafis.meta.Parameter;
import com.campustechng.aminu.idpenrollment.sourceafis.templates.MinutiaType;
import com.campustechng.aminu.idpenrollment.sourceafis.templates.TemplateBuilder;
import com.campustechng.aminu.idpenrollment.sourceafis.meta.*;
import com.campustechng.aminu.idpenrollment.sourceafis.extraction.filters.Equalizer;

import java.text.SimpleDateFormat;
import java.util.Date;



public class Extractor {
	@DpiAdjusted
	@Parameter(lower = 8, upper = 32)
	public int BlockSize = 15;

	public DpiAdjuster DpiAdjuster = new DpiAdjuster();
	@Nested
	public LocalHistogram Histogram = new LocalHistogram();
	@Nested
	public SegmentationMask Mask = new SegmentationMask();
	@Nested
	public Equalizer Equalizer = new Equalizer();
	@Nested
	public HillOrientation Orientation = new HillOrientation();
	@Nested
	public OrientedSmoother RidgeSmoother = new OrientedSmoother();
	@Nested
	public OrientedSmoother OrthogonalSmoother = new OrientedSmoother();
	@Nested
	public ThresholdBinarizer Binarizer = new ThresholdBinarizer();
	@Nested
	public VotingFilter BinarySmoother = new VotingFilter();
	@Nested
	public Thinner Thinner = new Thinner();
	@Nested
	public CrossRemover CrossRemover = new CrossRemover();
	@Nested
	public RidgeTracer RidgeTracer = new RidgeTracer();
	@Nested
	public InnerMask InnerMask = new InnerMask();
	@Nested
	public MinutiaMask MinutiaMask = new MinutiaMask();
	@Nested
	public DotRemover DotRemover = new DotRemover();
	@Nested
	public PoreRemover PoreRemover = new PoreRemover();
	@Nested
	public GapRemover GapRemover = new GapRemover();
	@Nested
	public TailRemover TailRemover = new TailRemover();
	@Nested
	public FragmentRemover FragmentRemover = new FragmentRemover();
	@Nested
	public BranchMinutiaRemover BranchMinutiaRemover = new BranchMinutiaRemover();
	@Nested
	public MinutiaCollector MinutiaCollector = new MinutiaCollector();
	@Nested
	public MinutiaShuffler MinutiaSorter = new MinutiaShuffler();
	@Nested
	public StandardDpiScaling StandardDpiScaling = new StandardDpiScaling();
	@Nested
	public MinutiaCloudRemover MinutiaCloudRemover = new MinutiaCloudRemover();
	@Nested
	public UniqueMinutiaSorter UniqueMinutiaSorter = new UniqueMinutiaSorter();

	public DetailLogger.Hook Logger = DetailLogger.off;

	public Extractor() {
		RidgeSmoother.Lines.StepFactor = 1.59f;
		OrthogonalSmoother.AngleOffset = Angle.PIB;
		OrthogonalSmoother.Lines.Radius = 4;
		OrthogonalSmoother.Lines.AngularResolution = 11;
		OrthogonalSmoother.Lines.StepFactor = 1.11f;
		BinarySmoother.Radius = 2;
		BinarySmoother.Majority = 0.61f;
		BinarySmoother.BorderDistance = 17;
	}

	public TemplateBuilder Extract(final byte[][] invertedImage, final int dpi) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println("Start: " + df.format(new Date()));
		final TemplateBuilder template = new TemplateBuilder();

		DpiAdjuster.Adjust(this, dpi, new Action() {
			@Override
			public void function() {
				byte[][] image = ImageInverter.GetInverted(invertedImage);
//				DetailLogger.log2D(image, "image_j.csv");

				BlockMap blocks = new BlockMap(new Size(image[0].length,
						image.length), BlockSize);
//				Logger.log("BlockMap", blocks);

				short[][][] histogram = Histogram.Analyze(blocks, image);
//				DetailLogger.log3D(histogram, "hist_j.csv");
				short[][][] smoothHistogram = Histogram.SmoothAroundCorners(
						blocks, histogram);
//				DetailLogger.log3D(smoothHistogram, "smhist_j.csv");
				BinaryMap mask = Mask.ComputeMask(blocks, histogram);
				float[][] equalized = Equalizer.Equalize(blocks, image,
						smoothHistogram, mask);

				byte[][] orientation = Orientation.Detect(equalized, mask,
						blocks);
				float[][] smoothed = RidgeSmoother.Smooth(equalized,
						orientation, mask, blocks);
				float[][] orthogonal = OrthogonalSmoother.Smooth(smoothed,
						orientation, mask, blocks);

				BinaryMap binary = Binarizer.Binarize(smoothed, orthogonal,
						mask, blocks);

				binary.AndNot(BinarySmoother.Filter(binary.GetInverted()));
				binary.Or(BinarySmoother.Filter(binary));
//				Logger.log("BinarySmoothingResult", binary);
				CrossRemover.Remove(binary);

				BinaryMap pixelMask = mask.FillBlocks(blocks);
				BinaryMap innerMask = InnerMask.Compute(pixelMask);

				BinaryMap inverted = binary.GetInverted();
				inverted.And(pixelMask);

				SkeletonBuilder ridges = null;
				SkeletonBuilder valleys = null;
				ridges = ProcessSkeleton("Ridges", binary);
				valleys = ProcessSkeleton("Valleys", inverted);

				template.originalDpi = dpi;
				template.originalWidth = invertedImage[0].length;
				template.originalHeight = invertedImage.length;

				MinutiaCollector.Collect(ridges, MinutiaType.Ending, template);
				MinutiaCollector.Collect(valleys, MinutiaType.Bifurcation,
						template);
				MinutiaMask.Filter(template, innerMask);
				StandardDpiScaling.Scale(template);
				MinutiaCloudRemover.Filter(template);
				UniqueMinutiaSorter.Filter(template);
				MinutiaSorter.Shuffle(template);
//				Logger.log("FinalTemplate", template);
			}
		});
		System.out.println("End: " + df.format(new Date()));
		return template;
	}

	SkeletonBuilder ProcessSkeleton(final String aName, final BinaryMap binary) {
		SkeletonBuilder skeleton = null;
		Logger.log("Binarized", binary);
		BinaryMap thinned = Thinner.Thin(binary);

		skeleton = new SkeletonBuilder();
		RidgeTracer.Trace(thinned, skeleton);
		DotRemover.Filter(skeleton);
		PoreRemover.Filter(skeleton);
		GapRemover.Filter(skeleton);
		TailRemover.Filter(skeleton);
		FragmentRemover.Filter(skeleton);
		BranchMinutiaRemover.Filter(skeleton);
		return skeleton;
	}

}
