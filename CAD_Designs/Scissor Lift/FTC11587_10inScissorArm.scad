/*
Robot scissor lift components for FTC Team #11587
10 inch Scissor Lift Arm
Kurt Kochendarfer
January 2017
*/

/*Initial scissor lift arm design.  Dimensionally restricted to 10" for printing on a 3D print bed.*/


module makeLowerFlange() {
    color("black",0.75) {
        cube(size=[240,8,4], center = true);
    }
}

module makeUpperFlange() {
    color("black",0.75) {
        translate ([0,0,8])
            cube(size=[240,8,4], center = true);
    }
}

module makeWeb() {
    color("black",0.75) {
        translate([0,0,4])
            cube(size=[240,4,8], center = true);
    }
}

module makeLHole() {
    color("blue",0.75) {
        translate([-120,0,4])
            rotate([90,0,0]) {
                cylinder (h=10,d=3,center=true,$fn=30);
            }
    }
}

module makeRHole() {
    color("blue",0.75) {
        translate([120,0,4])
            rotate([90,0,0]) {
                cylinder (h=10,d=3,center=true,$fn=30);
            }
            
    }
}

module makeMiddleHole() {
    color("blue",0.75) {
        translate([0,0,4])
            rotate([90,0,0]) {
                cylinder (h=10,d=3,center=true,$fn=30);
            }
    }
}
    
module makeRoundLEnd() {
    color("black",0.75) {
        translate([-120,0,4])
            rotate([90,0,0]) {
                cylinder (h=8,d=12,center=true,$fn=30);
            }
    }
}

module makeRoundREnd() {
    color("black",0.75) {
        translate([120,0,4])
            rotate([90,0,0]) {
                cylinder (h=8,d=12,center=true,$fn=30);
            }
            
        }    
}

module makeRoundMiddle() {
    color("black",0.75) {
        translate([0,0,4])
            rotate([90,0,0]) {
                cylinder (h=8,d=12,center=true,$fn=30);
            }
    }
}


module makeIBeam() {
    makeLowerFlange();
    makeUpperFlange();
    makeWeb();
    makeRoundLEnd();
    makeRoundREnd();
    makeRoundMiddle();
}

module makeHoles() {
    makeLHole();
    makeRHole();
    makeMiddleHole();
}

module makeObject() {
    difference() {
        makeIBeam();
        makeHoles();
    }
}

translate([0,0,2])
    makeObject();




