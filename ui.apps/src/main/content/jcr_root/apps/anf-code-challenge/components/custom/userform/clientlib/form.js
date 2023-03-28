(function($, ns, channel, window, undefined) {
    $(document).ready(function() {

        $('.alert-success').hide();
        $('.alert-error').hide();
        $('#resourcePath').hide();

        var countryurl = $('#resourcePath').val() + "/_jcr_content.countries.json";
        $.ajax({
            type: "GET",
            url: countryurl,
            processData: false,
            success: function(resp) {
                populateJSON(JSON.parse(resp).countries);
            }
        });

        $("#submit").click(function(e) {
            e.preventDefault();
            var url = $('#resourcePath').val() + "/_jcr_content.user.txt";
            $.ajax({
                type: "POST",
                url: url,
                data: $('form').serialize(),
                processData: false,
                success: function(resp) {
                    $('.alert-error').hide();
                    $('.alert-success').show();
                },
                error: function(resp) {
                    $('.alert-success').hide();
                    $('.alert-error').show();
                }


            });

        });



    });

    function populateJSON(countriesJSON) {
        var select = $('coral-select').get(0);
        countriesJSON.forEach(function(value, index) {
            var selectItem = document.createElement('coral-select-item');
            var content = {};
            content.innerHTML = value.text;
            selectItem.value = value.value;
            selectItem.content = content;
            select.append(selectItem);

        });
    }

}(jQuery, Granite.author, jQuery(document), this));