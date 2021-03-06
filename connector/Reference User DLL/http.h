#ifndef __HTTP_H__
#define __HTTP_H__

void init_curl();
void clean_curl();

int http_post(const char *url, const char* data, char* res, unsigned int res_size);
int http_get(const char *url, char* res, unsigned int res_size);

#endif // __HTTP_H__