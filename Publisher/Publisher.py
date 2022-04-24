#!/usr/bin/env python
import pika


def pub(virtualhost, message, exchange_name):

    # Set the connection parameters to connect to rabbit-server1 on port 5672
    # on the / virtual host using the username "guest" and password "guest"
    username = 'student'
    password = 'student01'
    hostname = '128.163.202.50'

    credentials = pika.PlainCredentials(username, password)
    parameters = pika.ConnectionParameters(hostname,
                                           5672,
                                           virtualhost,
                                           credentials)

    connection = pika.BlockingConnection(parameters)

    channel = connection.channel()

    # exchange_name = 'patient_data'

    channel.exchange_declare(exchange=exchange_name, exchange_type='topic')

    routing_key = '#'

    channel.basic_publish(
         exchange=exchange_name, routing_key=routing_key, body=message)
    print(" [x] Sent %r:%r" % (routing_key, message))

    connection.close()

    def loadApps():
        for dir in _execommon.APPSDIR.iterdir():
            if not dir.is_dir():
                continue
            dirName = dir.name
            # logging.debug(f'Loading app "{dirName}".')
            moduleName = 'apps.{dirName}.app'
            try:
                module = importlib.import_module(moduleName)
            except ImportError:
                logging.exception('Error while trying to import app "{moduleName}":')
                continue
            if dirName == 'sys':
                flaskconst.flaskapp.APP.register_blueprint(module.c.BLUEPRINT)
            else:
                flaskconst.flaskapp.APP.register_blueprint(
                    module.c.BLUEPRINT,
                    url_prefix='/{dirName}',
                )
            flaskconst.APPS[dirName] = module
        # logging.debug('Synchronizing privilege entities...')
        appSys = flaskconst.APPS['sys']
        sysDbVer = appSys.c.DBVLATEST.V.versionStats()[0]

        if sysDbVer is not None and sysDbVer >= 13:  # Only do entity sync if the database is ready for it.
            entities = {entityType: set() for entityType in appSys.c.ENTITYTYPE}
            for app in flaskconst.APPS.values():
                for entityType in appSys.c.ENTITYTYPE:
                    entities[entityType].update(app.c.ENTITIES.get(entityType, {}))
            appSys.db.accessEntitiesSet(entities)

        if sysDbVer is not None and sysDbVer >= 14:  # Only do admin account creation if the database is ready for it.
            try:
                appSys.db.accountGetByUsername(appSys.c.ACCOUNTUSERNAMESTARTER)
            except KeyError:
                appSys.db.accountNew(
                    appSys.c.ACCOUNTUSERNAMESTARTER,
                    'Delete',
                    'Me',
                    'Delete This Account',
                    security.LOGINMETHODS.BASIC,
                    None,  # Change this arg to a strong `str` password.  Intentionally `None` to throw exception.
                    _BYPASSAUTHORIZATION=True,
                )
        logging.info('App loading complete.  Current list is: {flaskconst.APPS!r}')
