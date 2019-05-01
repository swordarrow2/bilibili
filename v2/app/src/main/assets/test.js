function test(){
            document.getElementById("p").innerHTML += " 你好！"
			var c=document.getElementById("myCanvas");
var ctx=c.getContext("2d");
document.getElementById("p").innerHTML += OCRAD(ctx.getImageData(0, 0, 120, 40));
        }
