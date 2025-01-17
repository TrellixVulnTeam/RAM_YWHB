// Enlarged Rostock Motor End inspired from Jcrocholl modified by Kolergy to:
//    - Take 12x1000mm smooth rods and KB-12-WW Linear Bearings
//    - Should clip the bearings (Not yet tested)
//    - 0.10° bearing holder angles to compensate play
//    - Changed droove to improve fabricability
//    - T2.5 self locking belt mount based on belt_coupler_v6 from Okatake

 


use <Linear_Bearing.scad>
use <belt_coupler_v6_Mod.scad>

$fn=75;
// Rostock Motor End from Jcrocholl modified by Kolergy to:
//    - Take 12x1000mm smooth rods and KB-12-WW Linear Bearings
//    - Increased base height to locate electronics below the base
//    - Stand on three feets


motor_end_height      =  64;  // Was 44
idler_end_height      =  28;
carriage_height       =  80;//32; // Adapted to cover full length +2 of the Linear bearing
platform_thickness    =   8;
bed_thickness         =  12;
pcb_thickness         =   2;
smooth_rod_length     =1000;
platform_hinge_offset =  33;
carriage_hinge_offset =  24;
tower_radius          = 300;
rod_length            = tower_radius * 1.44;

H  = carriage_height;
W  = 60;
d  = 22;
th = 4;

width = 90;                  // Caridge widith
//height = carriage_height;

offset = 25;
cutout = 13;                 // Universal joint cutout
middle = 2*offset - width/2;


//% translate([ W/2, 0, 0]) linear_bearing("KB-12-WW");
//% translate([-W/2, 0, 0]) linear_bearing("KB-12-WW");

module magnete() {
rotate([-135])
difference() {
	translate([0,0,0]) cylinder(r=13, h=9, center=false);
	union(){
	translate([0,0,3]) cylinder(r=10.4, h=7, center=true);
	translate([0,0,0]) cylinder(r=5, h=99, center=false);
}
} 
}



module conical_groove(d1, d2, w) {
	difference() {
		translate([0, 0, -w  ]) cylinder(r =d1/2+0.1,       h=w*2,     center=true);
		translate([0, 0, -w/2]) cylinder(r1=d2/2  ,r2=d1/2, h=w+0.1,   center=true);
		translate([0, 0, -w  ]) cylinder(r =d2/2,           h=w*2+0.1, center=true);
	}
}

module groove(d1, d2, w) {
		difference() {
			cylinder(r=d1/2+1, h=w, center=true);
			cylinder(r=d2/2  , h=w, center=true);
			translate([0, 0, w/2]) cylinder(r1=d2/2  ,r2=d1/2, h=w/1.5,   center=true);
		}
}

module LB_holder(d, w, H, dH) {
	difference() {
		union() {
			difference() {
				translate([ w,   0, 0]) cylinder(r=d/2+th, h=H+dH,   center=true);
				translate([ w,   0, 0]) cylinder(r=d/2,    h=H+dH+2, center=true);
			}
			translate([ w,  0, -H/2])  conical_groove(d, d-2, dH/4);
			translate([ w,  0,  H/2])  rotate([ 180,  0,  0]) conical_groove(d, d-2, dH/4);
		}
		//translate([ w,  0,  H/2-5])  groove(d+2*th+1, d+2*th-4, 3);
		//translate([ w,  0,  -H/2+5])  groove(d+2*th+1, d+2*th-4, 3);
		translate([ w, d/2+3, 0]) cube([d+2*th+2, d, H+dH+2], center=true);
	}
}

module parallel_joints(reinforced) {
  difference() {
    union() {
      intersection() {   // UJoint Arms
        cube([width, 20, 8], center=true);
        rotate([0, 90, 0]) cylinder(r=4.7, h=width, center=true);
      }
      intersection() {
        translate([0, 18, 4]) rotate([45, 0, 0])
          cube([width, reinforced+13, reinforced+13], center=true);
        translate([0, 0, 20]) cube([width, 35+5, 40+8], center=true);
      }
      translate([0, 8, 0]) cube([width, 16, 8], center=true);
    }
    //rotate([0, 90, 0]) cylinder(r=1.55, h=80, center=true, $fn=12);

    for (x = [-offset, offset]) {
      translate([x*2.2, 14, 0])
        cylinder(r=14, h=100, center=true, $fn=24);
      translate([(x*2.2), -4.5, 0])
        cube([12, 30, 100], center=true);
      //translate([x, 0, 0]) rotate([0, 90, 0]) rotate([0, 0, 30])
        //cylinder(r=3.3, h=17, center=true, $fn=6);
    }
    translate([0, -22, 0]) resize([15,75,0]) cylinder(r=20, h=100, center=true);
  }
}

module belt_mount() {
  difference() {
    union() {
      difference() {
        translate([8, 0, 0]) cube([4, 14, H+4], center=true);
        for (z = [-3.5, 3.5])
          translate([8, 0, z]) cube([5, 14, 3], center=true);
      }
      for (y = [-2.5, 1.5, 6.5]) {
        translate([8, y, 0]) cube([4, 1.2, H+4], center=true);
      }
    }
  }
}

module belt_mount2() {
        translate([8, 8, 0]) 
	difference() {
		union() {
			rotate([90, -90, 0]) coupler();
			translate([0, -17, 0]) cylinder(r=5, h=H+th, center=true, $fn=12);
		}
      	for (y = [-0.7, 0.7]) {
        		translate([0, -3, y*H/2]) rotate([0, 90, 0]) cylinder(r=1.6, h=5, center=true, $fn=12);
		}
	}
}

module belt_mount3() {
	difference(){
		union(){
			translate([9, -5.25, 0]) cube([17, 4.5, H+th], center=true);
			translate([9, 0, (H+th)/2-7]) 
			difference() {
				translate([0, 0, -10])cube([17, 7, 34], center=true);
				translate([0, 2, -25]) rotate([-45, 0,  0]) cube([18, 6, 12], center=true);
			}
		translate([9, 0, -(H+th)/2+12.5]) cube([17, 6, 25], center=true);
		}
	translate([12.0,  0, 0]) cylinder(r=1.6, h=H+th+2, center=true, $fn=12);
	translate([ 3.5,  0, 0]) cylinder(r=1.6, h=H+th+2, center=true, $fn=12);
    translate([ 7.8, 14.35, -(H+th)/2+1]) rotate([90, -90, 0]) belt();
	translate([ 8.2, 13.85, -(H+th)/2+1]) rotate([90, -90, 0]) belt();
    translate([ 7.8, 14.35, -(H-85+th)/2+1]) rotate([90, -90, 0]) belt();
	translate([ 8.2, 13.85, -(H-85+th)/2+1]) rotate([90, -90, 0]) belt(); // Slightly enlarge the gap of the belt
	translate([8, 0.5, -(H+th)/2+2.5]) cube([3, 6, 6], center=true);
	}
}
module carriage() {
	difference() {
		union() {
			difference() {
				union() {
					translate([ 0,  -th-7, 0]) cube([W, 8, H+4], center=true);
					translate([ 0,  -28,  -H/2+2])  parallel_joints(16);
				}
				translate([ W/2,   0, 0]) cylinder(r=d/2+1,    h=H+4+2, center=true);
				translate([-W/2,   0, 0]) cylinder(r=d/2+1,    h=H+4+2, center=true);
			}
			// 0.10° rotation to compensate play on different axis
			rotate([0, 0.10,  0]){ 
                LB_holder(d,  W/2, H, 4);
                translate([ 0,   0, -24])  LB_holder(d,  W/2, 32, 4); 
                translate([ 0,   0, 24]) LB_holder(d,  W/2, 32, 4);
            } 
			rotate([0.10,  0, 0]){
                LB_holder(d, -W/2, H, 4);
                translate([ 0,   0, -24])  LB_holder(d,  -W/2, 32, 4); 
                translate([ 0,   0, 24]) LB_holder(d,  -W/2, 32, 4);
            }
    			belt_mount3();
		}
		// Grooves
		translate([ W/2,  0,  H/2-5])  groove(d+2*5+1, d+2*th-4, 4);
		translate([ W/2,  0, -H/2+5])  groove(d+2*5+1, d+2*th-4, 4);
		translate([-W/2,  0,  H/2-5])  groove(d+2*5+1, d+2*th-4, 4);
		translate([-W/2,  0, -H/2+5])  groove(d+2*5+1, d+2*th-4, 4);
        		// Grooves
        
		translate([ W/2,  0,  H/2-20])  groove(d+2*5+1, d+2*th-4, 4);
		translate([ W/2,  0, -H/2+25])  groove(d+2*5+1, d+2*th-4, 4);
		translate([-W/2,  0,  H/2-25])  groove(d+2*5+1, d+2*th-4, 4);
		translate([-W/2,  0, -H/2+25])  groove(d+2*5+1, d+2*th-4, 4);
        
		// Screw hole for adjustable top endstop.
		//translate([15, -17, -H/2+4]) cylinder(r=1.5, h=40, center=true, $fn=12);
	}

}
//translate([ w,  0, 0]) cylinder(r=11  , h=H+dH+2, center=true);
//import("/home/andrea/git/RAM/parti/carrello.stl");
module carrello() {
//difference(){
	union(){
		intersection(){
			carriage();
			union() {
				translate([-27,-25,-22]) cylinder(r=16, h=40, center=true);
				translate([27,-25,-22]) cylinder(r=16, h=40, center=true);
				cube([100, 40, 100], center=true);
				translate([0,0,-35]) cube([50, 100, 10], center=true);
			}
		}
		translate([-27,-30,-22]) magnete();
		translate([27,-30,-22]) magnete();
	}
//translate([15, -17, 2]) cylinder(r=3.2, h=8, center=true, $fn=6);
//}
//#translate([-27,-30,0])cube([54,2,2]);

// Uncomment the following lines to check endstop alignment.
// use <idler_end.scad>;
// translate([0, 0, -20]) rotate([180, 0, 0]) idler_end();
}
module supporto_carrello(){
    difference(){
        carrello();
        union(){
            translate([0,0,-32]) rotate([90, 0, 0])cylinder(r=2.6, h=40, center=true);
            translate([0,0,-8]) rotate([90, 0, 0])cylinder(r=2.6, h=40, center=true);
                translate([0,0,18]) rotate([90, 0, 0])cylinder(r=2.6, h=40, center=true);
        }
    }            
}
supporto_carrello();