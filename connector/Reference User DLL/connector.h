#ifndef __CONNECTOR_H__
#define __CONNECTOR_H__

bool is_connector_enabled();
void init_connector();
void clean_connector();
double process_query(const char* pquery);

#endif // __CONNECTOR_H__