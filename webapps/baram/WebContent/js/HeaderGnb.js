$(function() {
	$("#NoticeClick").click(function() {
		$(".NoticeClickSub").css("display", "block");
		$(".HelpClickSub").css("display", "none");
		$(".UserClickSub").css("display", "none");
	});
	$(".NoticeClickSubTop").click(function() {
		$(".NoticeClickSub").css("display", "none");
	});

	$("#FeedbackClick").click(function() {
		$("#FeedbackPopupWrap").css("display", "block");
		$(".HelpClickSub").css("display", "none");
		$(".NoticeClickSub").css("display", "none");
		$(".UserClickSub").css("display", "none");
	});
	$("#FeedbackPopupbg").click(function() {
		$("#FeedbackPopupWrap").css("display", "none");
	});

	$("#HelpClick").click(function() {
		$(".HelpClickSub").css("display", "block");
		$(".NoticeClickSub").css("display", "none");
		$(".UserClickSub").css("display", "none");

	});
	$(".HelpClickSubTop").click(function() {
		$(".HelpClickSub").css("display", "none");
	});

	$("#UserClick").click(function() {
		$(".UserClickSub").css("display", "block");
		$(".NoticeClickSub").css("display", "none");
		$(".HelpClickSub").css("display", "none");
	//	$(".UserClickSub").css("margin-left", "340px");
	});
	$(".UserClickSubTop").click(function() {
		$(".UserClickSub").css("display", "none");
	});

	$("#btnClick").click(function() {
		$("#btnPopupWrap").css("display", "block");
		$(".HelpClickSub").css("display", "none");
		$(".NoticeClickSub").css("display", "none");
		$(".UserClickSub").css("display", "none");
	});
	$("#btnPopupbg").click(function() {
		$("#btnPopupWrap").css("display", "none");
	});

});