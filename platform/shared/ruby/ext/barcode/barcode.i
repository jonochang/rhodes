/* barcode.i */
%module Barcode
%{

extern void rho_barcode_scan_image (char* callback_url, char *file_path, int withPreview);
#define scan_image rho_barcode_scan_image

extern void rho_barcode_scan_camera (char* callback_url);
#define scan_camera rho_barcode_scan_camera

%}

extern void scan_image  (char* callback_url, char *file_path, int withPreview);
extern void scan_camera (char* callback_url);
