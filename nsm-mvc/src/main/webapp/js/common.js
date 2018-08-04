/**
 * Created by nieshuming on 2018/8/4.
 */

/**
 * html字符串转dom对象
 * @param html
 * @returns {NodeList}
 */
function parseDom(html) {
    var div = document.createElement("div");
    div.innerHTML = html;
    return div.childNodes;
}

function createShowModal(){
    <!-- 模态框 -->
    var html =
        '<div class="modal" tabindex="-1" role="dialog" id="showModal">' +
            '<div class="modal-dialog modal-dialog-centered" role="document">'+
                '<div class="modal-content">'+
                    '<div class="modal-header alert">'+
                        '<h5 class="modal-title "></h5>'+
                        '<button type="button" class="close" data-dismiss="modal" aria-label="Close">'+
                            '<span aria-hidden="true">&times;</span>'+
                        '</button>' +
                    '</div>' +
                    '<div class="modal-body"></div>'+
                    '<div class="modal-footer">'+
                    '<button type="button" class="btn btn-info" data-dismiss="modal">关闭</button>'+
                    '</div>'+
                '</div>'+
            '</div>'+
        '</div>';
    return parseDom(html)[0];
}

function modalShow(title, content, warning){
    var modal = document.getElementById("showModal");
    if(!modal) {
        modal = createShowModal();
        document.body.appendChild(modal);
    }
    $(modal).find(".alert").removeClass("alert-success alert-warning");
    $(modal).find(".modal-body").removeClass("text-success text-danger");
    if(warning) {
        $(modal).find(".alert").addClass("alert-warning");
        $(modal).find(".modal-body").addClass("text-danger");
    }else {
        $(modal).find(".alert").addClass("alert-success");
        $(modal).find(".modal-body").addClass("text-success");
    }
    $(modal).find(".modal-title").text(title);
    $(modal).find(".modal-body").text(content);
    $(modal).modal('show');
}
