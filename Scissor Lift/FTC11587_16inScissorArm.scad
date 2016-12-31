/*
Robot scissor lift components for FTC Team #11587
16 inch Scissor Lift Arm
Kurt Kochendarfer
January 2017
*/

/*Scissor lift arm design in 2 pieces to allow printing on a 10" print bed 3D printer.*/

module makeLowerFlange() {
    translate([-114,0,0])
    color("black",0.75) {
        cube(size=[228,12.5,4],center=true);
    }
}

module makeUpperFlange() {
    translate([-114,0,10])
    color("black",0.75) {
        cube(size=[228,12.5,4],center=true);
    }
}

module makeWeb() {
    translate([-114,0,5])
    color("black",0.75) {
        cube(size=[228,6,10],center=true);
    }
}

module makeBeam() {
    makeLowerFlange();
    makeUpperFlange();
    makeWeb();
}

module makeEndHole() {
    translate([-228,0,5])
    color("blue",0.75) {
        rotate([90,0,0]) {  
            cylinder(h=14,d=4,center=true,$fn=30);
        }
    }
}

module makeRoundEnd() {
    translate([-228,0,5])
    color("black",0.75) {
        rotate([90,0,0]) {
            cylinder(h=12.5,d=14,center=true,$fn=30);
        }
    }
}

module makeConnectHole1() {
    translate([-55,0,5])
    color("blue",0.75) {
        rotate([90,0,0]) {  
            cylinder(h=14,d=4,center=true,$fn=30);
        }
    }
}

module makeConnectHole2() {
    translate([-5,0,5])
    color("blue",0.75) {
        rotate([90,0,0]) {  
            cylinder(h=14,d=4,center=true,$fn=30);
        }
    }
}

module makePivotHole() {
    translate([-30,0,5])
    color("blue",0.75) {
        rotate([90,0,0]) {  
            cylinder(h=14,d=4,center=true,$fn=30);
        }
    }
}

module makePivotReinforcement() {
    translate([-30,0,5])
    color("black",0.75) {
        rotate([90,0,0]) {
            cylinder(h=12.5,d=14,center=true,$fn=30);
        }
    }
}
    
module makeCutaway() {
    translate([-60,0,-3])
    color("yellow",0.75) {
        cube(size=[61,7,16]);
    }
}

module makeAllBeam() {
    makeBeam();
    makeRoundEnd();
    makePivotReinforcement();
}

module makeAllHoles() {
    makeEndHole();
    makeConnectHole1();
    makeConnectHole2();
    makePivotHole();
}

module makeOverlap() {
    difference() {
        makeAllBeam();
        makeCutaway();
    }
}

translate([0,0,2])
difference() {
    makeOverlap();
    makeAllHoles();
}
    