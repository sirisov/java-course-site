<!DOCTYPE html>
<html lang="ru">
  <#include "head.ftl">
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
              <a data-toggle="collapse" data-target="#${group?replace(" ","_")}">&nbsp;&nbsp;<i class="glyphicon glyphicon-list"></i> ${group} <span class="arrow glyphicon glyphicon-chevron-down"></span></a>
            </li>
            <ul class="sub-menu collapse" id="${group?replace(" ","_")}">
              <#list list as task>
              <li><a href="#" data-id="${task.id}" <#if task.preload?has_content>data-preload='${task.preload}'</#if> <#if task.info?has_content>data-information="${task.info}"</#if> data-description="${task.description}" data-name="${task.name?replace("* ", "")}" data-code='${task.code}'>&nbsp;&nbsp;&nbsp;&nbsp;<i class="glyphicon glyphicon-menu-right"></i> ${task.name?replace("*", "<i class='glyphicon glyphicon-star'></i>")}</a></li>
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
              </div>
              <div class="form-group" id="code_div">
                <textarea id="code_editor" class="form-control" rows="3"></textarea>
              </div>
              <div id="test_result"></div>
              <div class="btn-group" role="group">
                <button class="btn btn-primary" type="button" id='code_test'>Test your code</button>
                <button class="btn btn-default" type="button" id='code_reset'>Reset</button>
              </div>
            </form>
            <p id="task_info"><!--Info--></p>
            <p id="task_preload"><!--Preload--></p>
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
    <script type="text/javascript" src="//cdnjs.cloudflare.com/ajax/libs/highlight.js/9.10.0/highlight.min.js"></script>
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
        $('#training_tab span').html(task.data('description'));
        $('#task_info').html($('<div/>'));
        if (task.data('information')) {
          $('#task_info div').html(task.data('information'));
          $('#task_info pre code').each(function(i, block) {
            hljs.highlightBlock(block);
          });
        }
        $('#task_preload').html($('<div/>'));
        if (task.data('preload')) {
          task.data('preload').forEach(function(preload) {
            $('#task_preload').append('<pre><code class="language-java">' + preload + '</code></pre>');
          });
          $('#task_preload').prepend('<h4>Preloaded</h4>');
          $('#task_preload pre code').each(function(i, block) {
            hljs.highlightBlock(block);
          });
        }
        
        $('#code_reset').on('click', function() {
          $('.alert').remove();
          editor.getDoc().setValue(task.data('code'));
          editor.focus();
        });
        $('#code_test').unbind('click');
        $('#code_test').on('click', function() {
          $('.alert').remove();
          if (typeof(Storage) !== "undefined") {
            localStorage.setItem(task.data('id'), editor.getDoc().getValue());
          } else {
            Cookies.set(task.data('id'), editor.getDoc().getValue());
          }
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
              if (res["output"]) {
                $("#test_result").append('<div id="output" class="alert alert-info" role="alert"><strong>Output:</strong><br/><br/><pre>' + res["output"] + '</pre></div>');
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
        if (typeof(Storage) !== "undefined") {
          if (Cookies.get(task.data('id'))) {
            localStorage.setItem(task.data('id'), Cookies.get(task.data('id')));
            Cookies.remove(task.data('id'));
          }
          editor.getDoc().setValue(localStorage.getItem(task.data('id')) || task.data('code'));
        } else {
          editor.getDoc().setValue(Cookies.get(task.data('id')) || task.data('code'));
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
    });
    </script>
  
  </body>
</html>