
// Rostock Motor End from Jcrocholl modified by Kolergy to:
//    - Take 12x1000mm smooth rods and KB-12-WW Linear Bearings
//    - Increased base height to locate electronics below the base
//    - Stand on three feets


motor_end_height      =  64;  // Was 44
idler_end_height      =  28;
carriage_height       =  32; // Adapted to cover full length +2 of the Linear bearing
platform_thickness    =   8;
bed_thickness         =  12;
pcb_thickness         =   2;
smooth_rod_length     =1000;
platform_hinge_offset =  33;
carriage_hinge_offset =  22;
tower_radius          = 300;
rod_length            = tower_radius * 1.44;


coupler_w = carriage_height+4;
coupler_h = 4.5;
coupler_d = 15;

belt_w = 15.5;
belt_n = 16;
belt_margin = 0.50;

coupler();

// coupler
module coupler() {
	difference(){
		translate([0,-0.20,-coupler_d/2+belt_w])cube([coupler_w,coupler_h,coupler_d],center=true);
		//for (axis_x = [-1:2:1]) {
			translate([-20,0,-0.01])
				scale(1.02)belt_length(profile = "T2.5", belt_width = belt_w+1, n = belt_n);
		//}
	}
}

module belt() {
	scale(1.02)belt_length(profile = "T2.5", belt_width = belt_w+1, n = belt_n);
}

// belt
//for (axis_x = [-1:2:1]) {
//	translate([2.00*belt_n*axis_x-11.25,0,-0.01])
//		%scale(1.02)belt_length(profile = "T2.5", belt_width = belt_w+1, n = belt_n);
//}

//================================================
// Module Section
//================================================
//Outer Module
module belt_length(profile = "T2.5", belt_width = 6, n = 10)
{

			if ( profile == "T2.5" ) 
				{ 
					_belt_length(
						profile=profile, 
						n = n, 
						belt_width = belt_width,
						tooth_pitch = 2.5,
						backing_thickness = 0.6 + belt_margin
						);
				}
			if ( profile == "T5" ) 
				{ 
					_belt_length(
						profile=profile, 
						n = n, 
						belt_width = belt_width,
						tooth_pitch = 5,
						backing_thickness = 1 + belt_margin
						);
				}
			if ( profile == "T10" ) 
				{ 
					_belt_length(
						profile=profile, 
						n = n, 
						belt_width = belt_width,
						tooth_pitch = 10,
						backing_thickness = 2 + belt_margin
						);
				}
			if ( profile == "MXL" ) 
				{ 
					_belt_length(
						profile=profile, 
						n = n, 
						belt_width = belt_width,
						tooth_pitch = 2.032,
						backing_thickness = 0.64 + belt_margin
						);
				}


}

//inner module
module _belt_length(profile = "T2.5", n = 10, belt_width = 5, tooth_pitch = 2.5, backing_thickness = 0.6)
{

for( i = [0:n])
	{
		union(){

			if ( profile == "T2.5" ) { translate([tooth_pitch*i,0,0])T2_5(width = belt_width);}
			if ( profile == "T5" ) { translate([tooth_pitch*i,0,0])T5(width = belt_width);}
			if ( profile == "T10" ) { translate([tooth_pitch*i,0,0])T10(width = belt_width);}
			if ( profile == "MXL" ) { translate([tooth_pitch*i,0,0])MXL(width = belt_width);}
			translate([(tooth_pitch*i)-(tooth_pitch/2),-backing_thickness,0])cube([tooth_pitch,backing_thickness,belt_width]);
		}
	}
}

//Tooth Form modules - Taken from http://www.thingiverse.com/thing:1662
module T2_5(width = 2)
	{
	linear_extrude(height=width) polygon([[-0.839258,-0.5],[-0.839258,0],[-0.770246,0.021652],[-0.726369,0.079022],[-0.529167,0.620889],[-0.485025,0.67826],[-0.416278,0.699911],[0.416278,0.699911],[0.484849,0.67826],[0.528814,0.620889],[0.726369,0.079022],[0.770114,0.021652],[0.839258,0],[0.839258,-0.5]]);
	}

module T5(width = 2)
	{
	linear_extrude(height=width) polygon([[-1.632126,-0.5],[-1.632126,0],[-1.568549,0.004939],[-1.507539,0.019367],[-1.450023,0.042686],[-1.396912,0.074224],[-1.349125,0.113379],[-1.307581,0.159508],[-1.273186,0.211991],[-1.246868,0.270192],[-1.009802,0.920362],[-0.983414,0.978433],[-0.949018,1.030788],[-0.907524,1.076798],[-0.859829,1.115847],[-0.80682,1.147314],[-0.749402,1.170562],[-0.688471,1.184956],[-0.624921,1.189895],[0.624971,1.189895],[0.688622,1.184956],[0.749607,1.170562],[0.807043,1.147314],[0.860055,1.115847],[0.907754,1.076798],[0.949269,1.030788],[0.9837,0.978433],[1.010193,0.920362],[1.246907,0.270192],[1.273295,0.211991],[1.307726,0.159508],[1.349276,0.113379],[1.397039,0.074224],[1.450111,0.042686],[1.507589,0.019367],[1.568563,0.004939],[1.632126,0],[1.632126,-0.5]]);
	}

module T10(width = 2)
	{
	linear_extrude(height=width) polygon([[-3.06511,-1],[-3.06511,0],[-2.971998,0.007239],[-2.882718,0.028344],[-2.79859,0.062396],[-2.720931,0.108479],[-2.651061,0.165675],[-2.590298,0.233065],[-2.539962,0.309732],[-2.501371,0.394759],[-1.879071,2.105025],[-1.840363,2.190052],[-1.789939,2.266719],[-1.729114,2.334109],[-1.659202,2.391304],[-1.581518,2.437387],[-1.497376,2.47144],[-1.408092,2.492545],[-1.314979,2.499784],[1.314979,2.499784],[1.408091,2.492545],[1.497371,2.47144],[1.581499,2.437387],[1.659158,2.391304],[1.729028,2.334109],[1.789791,2.266719],[1.840127,2.190052],[1.878718,2.105025],[2.501018,0.394759],[2.539726,0.309732],[2.59015,0.233065],[2.650975,0.165675],[2.720887,0.108479],[2.798571,0.062396],[2.882713,0.028344],[2.971997,0.007239],[3.06511,0],[3.06511,-1]]);
	}

module MXL(width = 2)
	{
	linear_extrude(height=width) polygon([[-0.660421,-0.5],[-0.660421,0],[-0.621898,0.006033],[-0.587714,0.023037],[-0.560056,0.049424],[-0.541182,0.083609],[-0.417357,0.424392],[-0.398413,0.458752],[-0.370649,0.48514],[-0.336324,0.502074],[-0.297744,0.508035],[0.297744,0.508035],[0.336268,0.502074],[0.370452,0.48514],[0.39811,0.458752],[0.416983,0.424392],[0.540808,0.083609],[0.559752,0.049424],[0.587516,0.023037],[0.621841,0.006033],[0.660421,0],[0.660421,-0.5]]);
	}