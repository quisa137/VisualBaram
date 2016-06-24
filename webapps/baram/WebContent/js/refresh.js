$("#loadingImage").hide();

function getCsv() {
	$("#forCsv").submit();
}

function refreshChart(url) {
	$('#charting-frame').attr('src', url);
}

$(function() {
	function split(val) {
		return val.split(/,\s*/);
	}
	function extractLast(term) {
		return split(term).pop();
	}
	$("#campaign").bind(
			"keydown",
			function(event) {
				if (event.keyCode === $.ui.keyCode.TAB
						&& $(this).data("autocomplete").menu.active) {
					event.preventDefault();
				}
			}).click(function() {
		this.value = '';
	}).autocomplete({
		minLength : 2,
		source : function(request, response) {
			$.ajax({
				type : "POST",
				dataType : "JSON",
				url : "csv?dataType=meta",
				data : {
					term : '확' + extractLast(request.term)
				},
				success : response
			});
		}
	});
});

$(function() {

	$("#csv").button({
		text : false,
		icons : {
			primary : "ui-icon-document"
		}
	}).click(function() {
		$("#loadingImage").show();
		getCsv();
		$("#loadingImage").hide();
		return false;
	});

	$("#refresh-chart").button({
		text : false,
		icons : {
			primary : "ui-icon-play"
		}
	}).click(
			function() {
				var url = "csv?dataType=html&enc_check="
						+ $("#enc_check").val() + "&viewtype="
						+ $("#viewtype").val() + "&metric="
						+ $("#metric").val() + "&ismobile="
						+ $("#ismobile").val() + "&costtype="
						+ $("#costtype").val() + "&ctype=" + $("#ctype").val()
						+ "&zone=" + $("#zone").val() + "&client="
						+ $("#client").val() + "&campaign="
						+ $("#campaign").val() + "&like=" + $("#like").val()
						+ "&zonelike=" + $("#zonelike").val()
						+ "&categorylike=" + $("#categorylike").val()
						+ "&clientlike=" + $("#clientlike").val()
						+ "&campaignlike=" + $("#campaignlike").val()
						+ "&orderby=" + $("#orderby").val() + "&topn="
						+ $("#topn").val() + "&basis=" + $("#basis").val()
						+ "&groupby=" + $("#groupby").val();
				$("#loadingImage").show();
				refreshChart(url);
				$("#loadingImage").hide();
				return false;
			});
});