package com.badyouth.uploadplugin.model

data class CertificateModel(var id: String,
                            var type: String,
                            var short: String,
                            var cert: Cert)

data class Cert(var icon: Icon,
                var binary: Binary)

data class Icon(var key: String,
                var token: String,
                var upload_url: String)

data class Binary(var key: String,
                  var token: String,
                  var upload_url: String)