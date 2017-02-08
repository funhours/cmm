$(document).ready(function() {
	$('#upload').click(function() {
		upload($('#imageFile').val());   //函数参数为上传的文件的本机地址
	});
});

function upload(fileName) {
	$.ajaxFileUpload({
		url : 'upload/imageUpload',   //提交的路径
		secureuri : false, // 是否启用安全提交，默认为false
		fileElementId : 'imageFile', // file控件id
		dataType : 'json',
		data : {
			fileName : fileName   //传递参数，用于解析出文件名
		}, // 键:值，传递文件名
		success : function(data, status) {
			if (data.error == 0) {
				var src = data.src;
				$('.eventImg').attr("src",src);  //显示图片

				// 存储已上传图片地址
				var oldSrc = $('#pictureSrc').val();
				var newSrc = "";
				if (oldSrc != "")
					newSrc = oldSrc + ";" + src;
				else
					newSrc = src;

				$('#pictureSrc').val(newSrc);   //保存路径
			} else {
				alert(data.message);
			}
		},
		error : function(data, status) {
			alert(data.message);
		}
	});
}