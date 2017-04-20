<!DOCTYPE html>
<html lang="ru">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
    <title>Java ALE internal training</title>

    <!-- Bootstrap core CSS -->
    <link href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css" rel="stylesheet"/>
    <!-- Highlight -->
    <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/highlight.js/9.10.0/styles/default.min.css">
    <!-- Sidebar -->
    <link href="sidebar.css" rel="stylesheet"/>
    <!-- CodeMirror -->
    <link href="codemirror.css" rel="stylesheet"/>
    <link href="codemirror_bootstrap_theme.css" rel="stylesheet"/>
    
    
  </head>
  <body>
    <div class="nav-side-menu">
      <div class="brand">Java ALE trainings</div>
      <i class="glyphicon glyphicon-menu-hamburger toggle-btn" data-toggle="collapse" data-target="#menu-content"></i>
      <div class="menu-list">
        <ul id="menu-content" class="menu-content collapse out" role="tablist">
          <li data-toggle="collapse" data-target="#trainings_menu" class="collapsed active" role="presentation">
            <a href="#training_tab" aria-controls="training_tab" role="tab" data-toggle="tab"><i class="glyphicon glyphicon-education"></i> Trainings <span class="arrow glyphicon glyphicon-chevron-down"></span></a>
          </li>
          <ul class="sub-menu collapse in" id="trainings_menu">
            <#list tasks as group, list>
            <li>
              <a data-toggle="collapse" data-target="#java_base">&nbsp;&nbsp;<i class="glyphicon glyphicon-list"></i> ${group} <span class="arrow glyphicon glyphicon-chevron-down"></span></a>
            </li>
            <ul class="sub-menu collapse in" id="${group?replace(" "," ")}">
              <#list list as task>
              <li><a href="#" data-id="${task.id}" data-information="${task.info!''}" data-description="${task.description}" data-name="${task.name?replace("* ", "")}" data-code='${task.code}'>&nbsp;&nbsp;&nbsp;&nbsp;<i class="glyphicon glyphicon-menu-right"></i> ${task.name?replace("*", "<i class='glyphicon glyphicon-star'></i>")}</a></li>
              </#list>
            </ul>
            </#list>
          </ul>
          <li data-toggle="collapse" data-target="#presentations_menu" class="collapsed" role="presentation">
              <a href="#presentation_tab" aria-controls="presentation_tab" role="tab" data-toggle="tab"><i class="glyphicon glyphicon-film"></i> Presentations <span class="arrow glyphicon glyphicon-chevron-down"></span></a>
          </li>
          <ul class="sub-menu collapse" id="presentations_menu">
            <#list presentations as presentation>
              <li><a href="#" data-name="${presentation.name}" data-src="https://docs.google.com/presentation/d/${presentation.source}/embed"><i class="glyphicon glyphicon-menu-right"></i> ${presentation.name}</a></li>
            </#list>
          </ul>
        </ul>
      </div>
    </div>
    <div class="container" id="main">
      <div class="row">
        <div class="col-md-12 tab-content">
          <div role="tabpanel" class="tab-pane active" id="training_tab">
            <h4>Task</h4>
            <form>
              <div class="form-group">
                <span id="helpBlock" class="help-block">Description</span>
                <textarea id="code_editor" class="form-control" rows="3"></textarea>
                <p/>
                <div id="test_result"></div>
              </div>
              <div class="btn-group" role="group">
                <button class="btn btn-primary" type="button" id='code_test'>Test your code</button>
                <button class="btn btn-default" type="button" id='code_reset'>Reset</button>
              </div>
            </form>
            <p id="task_info"><!--Info--></p>
          </div>
          <div role="tabpanel" class="tab-pane" id="presentation_tab">
            <h4>Presentation</h4>
            <div class="embed-responsive embed-responsive-16by9">
              <iframe id="presentation_iframe" class="embed-responsive-item" allowfullscreen="true" mozallowfullscreen="true" webkitallowfullscreen="true">
                Here is presentation goes on.
              </iframe>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- Jquery & Bootstrap scripts -->
    <script type="text/javascript" src="//code.jquery.com/jquery-2.1.1.min.js"></script>
    <script type="text/javascript" src="//maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/highlight.js/9.10.0/highlight.min.js"></script>
    <script type="text/javascript" src="js.cookie.js"></script>
    <script type="text/javascript" src="codemirror.js"></script>
    <script type="text/javascript" src="clike.js"></script>
    <script>
    $( document ).ready(function() {
      var editor = CodeMirror.fromTextArea(document.getElementById('code_editor'), {
        lineNumbers: true,
        matchBrackets: true,
        mode: "text/x-java",
        autofocus: true
      });
      $('#trainings_menu > ul > li > a').on('click', function(e) {
        $('#trainings_menu > ul > li').removeClass('active');
        $(e.target.parentElement).addClass('active');
        var task = $(e.target);
        $('#training_tab h4').text(task.data('name'));
        $('#training_tab span').text(task.data('description'));
        $('#training_tab p#task_info').html($('<div/>').html(task.data('information')));
        $('#code_reset').on('click', function() {
          $('#test').remove();
          $('#compilation').remove();
          editor.getDoc().setValue(task.data('code'));
          editor.focus();
        });
        $('#code_test').unbind('click');
        $('#code_test').on('click', function() {
          $('#test').remove();
          $('#compilation').remove();
          Cookies.set(task.data('id'), editor.getDoc().getValue());
          $.ajax({
            type: "POST",
            url: "/code/" + task.data('id'),
            data: editor.getDoc().getValue(),
            beforeSend: function() { 
              $('#code_test').prop('disabled', true);
              $('#code_test').text('Testing...');
            },
            success: function(msg) {
              var res = JSON.parse(msg);
              if (res["compilation"] === "error") {
                $("#test_result").append('<div id="compilation" class="alert alert-danger" role="alert"><strong>Compilation failure!</strong><br/><br/><samp>' + res["message"].replace(/ /g, '&nbsp;').replace(/\r?\n/g, '<br />') + '</samp></div>');
              } else if (res["compilation"] === "success") {
                $("#test_result").append('<div id="compilation" class="alert alert-success" role="alert"><strong>Compilation succeeded!</strong><br/></div>');
              }
              if (res["test"] === "error") {
                $("#test_result").append('<div id="test" class="alert alert-danger" role="alert"><strong>Run failure!</strong><br/><br/><samp>' + res["message"].replace(/ /g, '&nbsp;').replace(/\r?\n/g, '<br />') + '</samp></div>');
              } else if (res["test"] === "failed") {
                $("#test_result").append('<div id="test" class="alert alert-danger" role="alert"><strong>Some tests failed</strong><br/><br/>' + res["short"] + '<br/><pre>' + res["result"] + '</pre></div>');
              } else if (res["test"] === "success") {
                $("#test_result").append('<div id="test" class="alert alert-success" role="alert"><strong>All tests passed!</strong><br/></div>');
              }
            },
            error: function (jqXHR, textStatus, errorThrown) {
              if (jqXHR.status == 500) {
                 $("#test_result").append('<div id="compilation" class="alert alert-danger" role="alert"><strong>' + $(new DOMParser().parseFromString(jqXHR.responseText, "text/html")).text() + '</strong><br/>Please contact site administrator</div>');
              }
            }
          });            
          $('#code_test').prop('disabled', false);
          $('#code_test').text('Test your code');
        });
        $('#code_reset').trigger('click');
        if (Cookies.get(task.data('id'))) {
          editor.getDoc().setValue(Cookies.get(task.data('id')));
        }
      })
      $('#presentations_menu > li > a').on('click', function(e) {
        $('#presentations_menu > li').removeClass('active');
        $(e.target.parentElement).addClass('active');
        $('#presentation_iframe').attr('src', $(e.target).data('src'));
        $('#presentation_tab h4').text($(e.target).data('name'));
      })
      $('#presentations_menu').on('show.bs.collapse', function (e) {
        $('#trainings_menu').collapse('hide');
        $('#presentations_menu > li> a:first').trigger('click');
      })
      $('#trainings_menu').on('show.bs.collapse', function (e) {
        $('#presentations_menu').collapse('hide');
      })
      hljs.initHighlightingOnLoad();
    });
    </script>
  
  </body>
</html>