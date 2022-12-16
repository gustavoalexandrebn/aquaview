def login_app(cpf, senha):
    from sqlite3 import connect
    conexao = connect('database.db')
    moradores = conexao.execute('SELECT * FROM morador')

    dict_moradores = moradores.fetchall()

    for m in dict_moradores:
        if cpf == m[0]:
            if senha == m[2]:
                return '{"status": "LA", "usuario": "' + m[1] + '", "endereco": "' + m[3] + '", "telefone": "' + m[
                    4] + '", "email": "' + m[5] + '"}'
            else:
                return "{\"status\":SI}"

    conexao.close()

    return "{\"status\":UNC}"


def get_clients_from_database():
    from sqlite3 import connect
    conn = connect('database.db')

    clients = conn.execute('SELECT * FROM morador')

    clients_dict = clients.fetchall()

    conn.close()

    return clients_dict


def get_volumes_from_database():
    from sqlite3 import connect
    conn = connect('database.db')

    clients = conn.execute('SELECT * FROM volume')

    clients_dict = clients.fetchall()

    conn.close()

    return clients_dict


def insert_new_volume(time, volume, id):
    from sqlite3 import connect
    conn = connect('database.db')

    sql_command = 'INSERT INTO historico (volumeatual, timestamp, idReservatorio) VALUES(' + str(volume) + ', "' + time + '",  "' + id + '")'
    conn.execute(sql_command)
    conn.commit()

    conn.close()


def get_last_volume_from_user(cpf):
    from sqlite3 import connect
    conn = connect('database.db')

    reservatorio = conn.execute('SELECT id, capacidade FROM reservatorio WHERE cpf="' + cpf + '"').fetchall()
    idReservatorio, capacidade = str(reservatorio[0][0]), reservatorio[0][1]
    volumes = conn.execute('SELECT volumeatual, timestamp FROM historico WHERE idReservatorio = "'+idReservatorio+'" '
                           'ORDER BY id DESC limit 1;')
    volume_tuple = volumes.fetchall()
    return capacidade, volume_tuple[0][0], volume_tuple[0][1]

def get_all_volume_from_user(cpf):
    from sqlite3 import connect
    conn = connect('database.db')

    reservatorio = conn.execute('SELECT id, capacidade FROM reservatorio WHERE cpf="' + cpf + '"').fetchall()
    idReservatorio = str(reservatorio[0][0])
    volumes = conn.execute('SELECT volumeatual FROM historico WHERE idReservatorio = "'+idReservatorio+'"').fetchall()
    data = 0
    for v in volumes:
        data += v[0]

    return data


def colete_volume_atual(user):
    capacidade, volume_atual, tempo_coleta = get_last_volume_from_user(user)

    return '{"capacidade": "' + str(capacidade) + '", "volumeatual": "' + str(
        volume_atual) + '", "tempocoleta": "' + str(tempo_coleta) + '"}'


def tuple_to_list(input_tuple, index):
    listagem = []
    for it in input_tuple:
        listagem.append(it[index])

    return listagem


def colete_dado_diario(cpf, timestamp):
    from sqlite3 import connect
    from datetime import datetime
    conn = connect('database.db')

    reservatorio = conn.execute('SELECT id FROM reservatorio WHERE cpf="' + cpf + '"').fetchall()
    idReservatorio = str(reservatorio[0][0])

    string = 'SELECT volumeatual FROM historico WHERE idReservatorio="' + idReservatorio + '"  and timestamp >=' + timestamp
    volumes = conn.execute(string).fetchall()
    list_volumes = tuple_to_list(volumes, 0)

    entrada = 0
    consumo = 0

    for index in range(0, len(list_volumes) - 1):
        if list_volumes[index] > list_volumes[index + 1]:
            consumo += list_volumes[index] - list_volumes[index + 1]
        elif list_volumes[index] < list_volumes[index + 1]:
            entrada += list_volumes[index + 1] - list_volumes[index]

    string = 'SELECT MAX(timestamp) FROM historico WHERE idReservatorio="' + idReservatorio + '" and timestamp >=' + timestamp + ''
    ultimo_timestamp = conn.execute(string).fetchall()[0][0]

    timestamp = datetime.fromtimestamp(ultimo_timestamp)

    return '{"consumo": "' + str(consumo) + '", "entrada": "' + str(entrada) + '", "data": "' + str(
        timestamp.strftime('%H:%M:%S %d-%m-%Y')) + '"}'


def gerar_grafico(list_consumo, list_volumes, list_timestamp, cpf, timestamp_inicial, timestamp_final, tipodedata,
                  data_type):
    import matplotlib.pyplot as plt
    import seaborn as sns
    import pandas as pd

    fig, ax = plt.subplots()

    if tipodedata in ['dia', 'semana', 'mes', 'ano']:

        obj = {}
        for lt in set(list_timestamp):
            obj[lt] = [0]

        for lc in list_consumo:

            if data_type == 'consumo':
                obj[list_timestamp[lc]][0] = (list_volumes[lc] - list_volumes[lc + 1]) + obj[list_timestamp[lc]][0]
            else:
                obj[list_timestamp[lc]][0] = (list_volumes[lc + 1] - list_volumes[lc]) + obj[list_timestamp[lc]][0]
    else:
        obj = {}
        for lc in list_consumo:
            print(list_timestamp[lc])
            obj[list_timestamp[lc]] = [list_volumes[lc]]

    if tipodedata == 'dia':
        for index in ['00:00', '00:30', '01:00', '01:30', '02:00', '02:30', '03:00', '03:30', '04:00', '04:30', '05:00',
                      '05:30', '06:00', '06:30', '07:00', '07:30', '08:00', '08:30', '09:00', '09:30', '10:00', '10:30',
                      '11:00', '11:30', '12:00', '12:30', '13:00', '13:30', '14:00', '14:30', '15:00', '15:30', '16:00',
                      '16:30', '17:00', '17:30', '18:00', '18:30', '19:00', '19:30', '20:00', '20:30', '21:00', '21:30',
                      '22:00', '22:30', '23:00', '23:30']:

            if index not in obj:
                obj[index] = [0]

        ordered_dict = {}
        for key in sorted(obj):
            ordered_dict[key] = obj[key]

        obj = ordered_dict

    if tipodedata == 'semana':
        obj_temp = {}
        for index in ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']:
            if index not in obj:
                if index == 'Mon':
                    obj_temp['Seg'] = [0]
                elif index == 'Tue':
                    obj_temp['Ter'] = [0]
                elif index == 'Wed':
                    obj_temp['Qua'] = [0]
                elif index == 'Thu':
                    obj_temp['Quin'] = [0]
                elif index == 'Fri':
                    obj_temp['Sex'] = [0]
                elif index == 'Sat':
                    obj_temp['Sab'] = [0]
                elif index == 'Sun':
                    obj_temp['Dom'] = [0]
            else:
                if index == 'Mon':
                    obj_temp['Seg'] = obj[index]
                elif index == 'Tue':
                    obj_temp['Ter'] = obj[index]
                elif index == 'Wed':
                    obj_temp['Qua'] = obj[index]
                elif index == 'Thu':
                    obj_temp['Quin'] = obj[index]
                elif index == 'Fri':
                    obj_temp['Sex'] = obj[index]
                elif index == 'Sat':
                    obj_temp['Sab'] = obj[index]
                elif index == 'Sun':
                    obj_temp['Dom'] = obj[index]

        obj = obj_temp

    if tipodedata == 'mes':
        max = 31

        if len(list_consumo) == 0:
            print('Lista zerada')
        elif ('01' or '03' or '05' or '07' or '08' or '10' or '12') in list_timestamp[list_consumo[0]][3:]:
            max = 32
        elif '02' in list_timestamp[list_consumo[0]][3:]:
            max = 29

        for index in range(1, max):
            if index < 10:
                if "0" + str(index) + str(list_timestamp[list_consumo[0]][2:]) not in obj:
                    obj["0" + str(index) + str(list_timestamp[list_consumo[0]][2:])] = [0]
            else:
                if str(index) + str(list_timestamp[list_consumo[0]][2:]) not in obj:
                    obj[str(index) + str(list_timestamp[list_consumo[0]][2:])] = [0]

        ordered_dict = {}
        for key in sorted(obj):
            ordered_dict[key] = obj[key]

        obj = ordered_dict

    if tipodedata == 'ano':
        obj_temp = {}
        for index in ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']:
            if index not in obj:
                if index == 'Feb':
                    obj_temp['Fev'] = [0]
                elif index == 'Apr':
                    obj_temp['Abr'] = [0]
                elif index == 'May':
                    obj_temp['Maio'] = [0]
                elif index == 'Aug':
                    obj_temp['Ago'] = [0]
                elif index == 'Sept':
                    obj_temp['Set'] = [0]
                elif index == 'Oct':
                    obj_temp['Out'] = [0]
                elif index == 'Dec':
                    obj_temp['Dez'] = [0]
                else:
                    obj_temp[index] = [0]
            else:
                if index == 'Feb':
                    obj_temp['Fev'] = obj[index]
                elif index == 'Apr':
                    obj_temp['Abr'] = obj[index]
                elif index == 'May':
                    obj_temp['Maio'] = obj[index]
                elif index == 'Aug':
                    obj_temp['Ago'] = obj[index]
                elif index == 'Sept':
                    obj_temp['Set'] = obj[index]
                elif index == 'Oct':
                    obj_temp['Out'] = obj[index]
                elif index == 'Dec':
                    obj_temp['Dez'] = obj[index]
                else:
                    obj_temp[index] = obj[index]
        obj = obj_temp

    obj['Seg'] = 0

    df = pd.DataFrame(data=obj).transpose()
    df.columns = ['consumo']
    sns.barplot(x=df.index, y=df['consumo'], ax=ax, data=df, palette="Blues_d")
    ax.set_ylabel('Litros')

    if tipodedata == 'dia':
        ax.set_xlabel('Horas')
    elif tipodedata == 'semana':
        ax.set_xlabel('Dias')
    elif tipodedata == 'mes':
        ax.set_xlabel('Dias')
    elif tipodedata == 'ano':
        ax.set_xlabel('Meses')

    if tipodedata == 'dia':
        ax.xaxis.set_major_locator(plt.MaxNLocator(6))

    elif tipodedata == 'mes':
        ax.xaxis.set_major_locator(plt.MaxNLocator(7))

    fig.tight_layout()
    name_fig = cpf + str(timestamp_inicial) + str(timestamp_final) + '.png'
    fig.savefig('imagens/' + name_fig)

    return name_fig


def timestamp_to_date(list_timestamp, tipodedata):
    from datetime import datetime

    for index in range(0, len(list_timestamp)):

        t = ''

        if tipodedata == 'dia':
            t = datetime.fromtimestamp(list_timestamp[index]).strftime("%H:%M")
        elif tipodedata == 'semana':
            t = datetime.fromtimestamp(list_timestamp[index]).strftime("%a")
        elif tipodedata == 'mes':
            t = datetime.fromtimestamp(list_timestamp[index]).strftime("%d/%m")
        elif tipodedata == 'ano':
            t = datetime.fromtimestamp(list_timestamp[index]).strftime("%b")

        list_timestamp[index] = t


def colete_consumo_entre_datas(cpf, data_inicial, data_final, tipodedata):
    from sqlite3 import connect
    conn = connect('database.db')

    import time
    import datetime
    timestamp_inicial = int(time.mktime(datetime.datetime.strptime(data_inicial, "%d/%m/%Y").timetuple()))
    timestamp_final = int(time.mktime(datetime.datetime.strptime(data_final, "%d/%m/%Y").timetuple()))

    reservatorio = conn.execute('SELECT id FROM reservatorio WHERE cpf="' + cpf + '"').fetchall()
    idReservatorio = str(reservatorio[0][0])
    string = 'SELECT volumeatual, timestamp FROM historico WHERE idReservatorio = "' + idReservatorio + '" and timestamp >=' + str(
        timestamp_inicial) + ' and timestamp <= ' + str(timestamp_final) + ''

    consulta = conn.execute(string).fetchall()
    list_volumes = tuple_to_list(consulta, 0)
    list_timestamp = tuple_to_list(consulta, 1)
    timestamp_to_date(list_timestamp, tipodedata)

    list_consumo = []
    consumo = 0
    qtd_consumo = 0

    for index in range(0, len(list_volumes) - 1):
        if list_volumes[index] > list_volumes[index + 1]:
            consumo += list_volumes[index] - list_volumes[index + 1]
            list_consumo.append(index)
            qtd_consumo += 1

    if tipodedata == 'dia':
        consumo_medio = consumo / 24
    elif tipodedata == 'semana':
        consumo_medio = consumo / 7
    elif tipodedata == 'mes':
        consumo_medio = consumo / 30
    else:
        consumo_medio = consumo / 12 if qtd_consumo > 0 else 0

    name_fig = gerar_grafico(list_consumo, list_volumes, list_timestamp, cpf, timestamp_inicial, timestamp_final,
                             tipodedata, 'consumo')

    return '{"consumo": "' + str(consumo) + '", "consumomedio": "' + str(
        consumo_medio) + '", "ulrfig": "' + name_fig + '"}'


def colete_entrada_entre_datas(cpf, data_inicial, data_final, tipodedata):
    from sqlite3 import connect
    conn = connect('database.db')

    import time
    import datetime
    timestamp_inicial = int(time.mktime(datetime.datetime.strptime(data_inicial, "%d/%m/%Y").timetuple()))
    timestamp_final = int(time.mktime(datetime.datetime.strptime(data_final, "%d/%m/%Y").timetuple()))

    reservatorio = conn.execute('SELECT id FROM reservatorio WHERE cpf="' + cpf + '"').fetchall()
    idReservatorio = str(reservatorio[0][0])
    string = 'SELECT volumeatual, timestamp FROM historico WHERE idReservatorio = "'+idReservatorio+'" and timestamp >=' + str(
        timestamp_inicial) + ' and timestamp <= ' + str(timestamp_final) + ''
    consulta = conn.execute(string).fetchall()
    list_volumes = tuple_to_list(consulta, 0)
    list_timestamp = tuple_to_list(consulta, 1)
    timestamp_to_date(list_timestamp, tipodedata)

    list_entrada = []
    qtd_entrada = 0
    entrada = 0

    for index in range(0, len(list_volumes) - 1):
        if list_volumes[index] < list_volumes[index + 1]:
            entrada += list_volumes[index + 1] - list_volumes[index]
            list_entrada.append(index)
            qtd_entrada += 1

    if tipodedata == 'dia':
        entrada_media = entrada / 24
    elif tipodedata == 'semana':
        entrada_media = entrada / 7
    elif tipodedata == 'mes':
        entrada_media = entrada / 30
    else:
        entrada_media = entrada / 12

    name_fig = gerar_grafico(list_entrada, list_volumes, list_timestamp, cpf, timestamp_inicial, timestamp_final,
                             tipodedata, '')

    return '{"consumo": "' + str(entrada) + '", "consumomedio": "' + str(
        entrada_media) + '", "ulrfig": "' + name_fig + '"}'


def get_medium_volume_value(user):
    return get_all_volume_from_user(user)
