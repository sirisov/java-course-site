<!DOCTYPE html>
<html lang="ru">
  <#include "head.ftl">
  <body>
    <div class="nav-side-menu">
      <div class="brand">Java ALE trainings</div>
      <i class="glyphicon glyphicon-menu-hamburger toggle-btn" data-toggle="collapse" data-target="#menu-content"></i>
      <div class="menu-list">
        <ul id="menu-content" class="menu-content collapse out">
          <#list tasks as group, list>
          <#if group != "Java8">
          <li>
            <a data-toggle="collapse" data-target="#${group?replace(" ","_")}">&nbsp;&nbsp;<i class="glyphicon glyphicon-list"></i> ${group} <span class="arrow glyphicon glyphicon-chevron-down"></span></a>
          </li>
          <ul class="sub-menu collapse in" id="${group?replace(" ","_")}">
            <#list list as task>
            <li><a href="#" data-id="${task.id}" data-information="${task.info!''}" data-description="${task.description}" data-name="${task.name?replace("* ", "")}" data-code='${task.code}'>&nbsp;&nbsp;&nbsp;&nbsp;<i class="glyphicon glyphicon-menu-right"></i> ${task.name?replace("*", "<i class='glyphicon glyphicon-star'></i>")}</a></li>
            </#list>
          </ul>
          </#if>
          </#list>
        </ul>
      </div>
    </div>
    <div class="container" id="main">
      <div class="row">
        <div class="col-md-12" id="content">
          <h4>Task</h4>
          <form>
            <div class="form-group">
              <span id="helpBlock" class="help-block">Description</span>
            </div>
            <div class="form-group">
              <textarea id="code_editor" class="form-control" rows="3"></textarea>
            </div>
            <div class="form-group">
              <select id="ip_select" class="form-control"></select>
            </div>
            <div id="test_result"></div>
          </form>
          <p id="task_info"><!--Info--></p>
        </div>
      </div>
    </div>
    <!-- Jquery & Bootstrap scripts -->
    <script type="text/javascript" src="//code.jquery.com/jquery-2.1.1.min.js"></script>
    <script type="text/javascript" src="//maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/highlight.js/9.10.0/highlight.min.js"></script>
    <script type="text/javascript" src="codemirror.js"></script>
    <script type="text/javascript" src="clike.js"></script>
    <script>
    $( document ).ready(function() {
      var editor = CodeMirror.fromTextArea(document.getElementById('code_editor'), {
        lineNumbers: true,
        matchBrackets: true,
        mode: "text/x-java",
        autofocus: true,
      });
      var ips = {"10.97.24.20" : "Sergey Irisov", "10.97.20.79" : "Olga Leonova", "10.97.24.17" : "Tatyana Speranskaya",
                 "10.97.20.139" : "Natalia Smirnova", "10.97.20.31": "Evgeny Efremenko", "10.97.24.22" : "Tatyana Gindysh",
                 "10.97.20.10" : "Kirill Shichalin", "10.97.20.135" : "Irina Germanova", "10.97.20.129" : "Sofiya Andreyuk",
                 "10.97.20.62" : "Alexey Belyakov", "10.97.20.128" : "Julia Saenko", "10.97.20.61" : "Anna Kulikova"};
      var resp = {<#list response as id, pair>"${id}":{<#list pair as ip, code>"${ip}":`${code}`, </#list>}, </#list>};
      $('#menu-content > ul > li > a').on('click', function(e) {
        $('#menu-content > ul > li').removeClass('active');
        $(e.target.parentElement).addClass('active');
        var task = $(e.target);
        $('#content h4').text(task.data('name'));
        $('#helpBlock').text(task.data('description'));
        var select = $('#ip_select');
        select.find('option').remove();
        select.append($('<option>', { value : task.data('code')})
            .text("Default"));
        if (resp[task.data('id')]) {
          $.each(resp[task.data('id')], function(key, value) {
            if ((key !== "0:0:0:0:0:0:0:1") && (key !== "127.0.0.1") ) {
              select
                .append($('<option>', { value : value})
                .text($.urlParam('anonymous') === "true" ? key : ips[key] || key)); 
            }
          });
        }
        $('#ip_select').unbind('change');
        $('#ip_select').on('change', function() {
          editor.getDoc().setValue(this.value);
        })
        $('#task_info').html($('<div/>').html(task.data('information')));
        editor.getDoc().setValue(task.data('code'));
        editor.focus();        
      })
      hljs.initHighlightingOnLoad();
      $.urlParam = function(name){
        var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);
        if (results==null){
          return null;
        }
        else{
          return results[1] || 0;
        }
      };
    });
    </script>
  </body>
</html>