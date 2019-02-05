<?php
defined('BASEPATH') OR exit('No direct script access allowed');


class Welcome extends CI_Controller {

	public function __construct()
	{
		parent::__construct();
		$this->load->helper('url');
		$this->load->library('session');
		$this->load->model('Query');
		date_default_timezone_set('Asia/Jakarta');
		header('Access-Control-Allow-Origin: *');
		header("Access-Control-Allow-Methods: GET, POST, OPTIONS, PUT, DELETE");
	}

	public function index()
	{
		echo 'hello';
	}


	// data menu
	public function menu()
	{
		$kategori 		= $this -> input -> get('kategori');

		if(isset($kategori)):
			$where = array('menu.id_menu_kategori'=>$kategori);
			$data['data']  = $this -> Query -> getDataJoinWhere('menu','menu_kategori','id_menu_kategori',$where) -> result();
		else:
			$where = array('is_avaible'=>true);
			$data['data']  = $this -> Query -> getDataJoinWhere('menu','menu_kategori','id_menu_kategori',$where) -> result();
		endif;
		if(count($data['data'])>=1):
			$data['status'] = 'ok';
			$data['msg']	= 'ok';
		else:
			$data['status'] = 'failed';
			$data['msg']	= 'data tidak ditemukan';
		endif;

		echo json_encode($data);
	}

	public function menu_detail()
	{
		$id 	= $this -> input -> get('id');
		if(isset($id )):
			$where = array('menu.id_menu'=>$id,'is_avaible'=>true);
			$data['data'] = $this -> Query -> getDataJoinWhere('menu','menu_kategori','id_menu_kategori',$where)->row();
			if(count($data['data'])>=1):
				$data['status'] = 'ok';
				$data['msg']	= 'ok';
			else:
				$data['status'] = 'failed';
				$data['msg']	= 'data tidak ditemukan';
			endif;
		else:
			$data['status'] = 'failed';
			$data['msg']	= 'id data tidak ditemukan';
		endif;
		echo json_encode($data);
	}

	public function menu_search()
	{
		$keyword 	= $this -> input -> get('keyword');
		if(isset($keyword)):
			$where = array('nama_menu'=>$keyword);
			$data['data'] = $this -> Query -> getDataJoinLike('menu','menu_kategori','id_menu_kategori',$where)->result();
			if(count($data['data'])>=1):
				$data['status'] = 'ok';
				$data['msg']	= 'ok';
			else:
				$data['status'] = 'failed';
				$data['msg']	= 'data tidak ditemukan';
			endif;
		else:
			$data['status'] = 'failed';
			$data['msg']	= 'mohon input kata kunci untuk melakukan pencarian';
		endif;
		echo json_encode($data);
	}
	// data menu

	// data kategori
	public function kategori()
	{
		$data['data']  = $this -> Query -> getAllData('menu_kategori') -> result();
		if(count($data['data'])>=1):
			$data['status'] = 'ok';
			$data['msg']	= 'ok';
		else:
			$data['status'] = 'failed';
			$data['msg']	= 'data tidak ditemukan';
		endif;
		echo json_encode($data);
	}
	// data kategori

	// data kategori
	public function meja()
	{
		$data['data']  = $this -> Query -> getAllData('meja') -> result();
		if(count($data['data'])>=1):
			$data['status'] = 'ok';
			$data['msg']	= 'ok';
		else:
			$data['status'] = 'failed';
			$data['msg']	= 'data tidak ditemukan';
		endif;
		echo json_encode($data);
	}
	// data kategori

	// data pesanan
	public function pesanan_add()
	{
		$meja 	= $this -> input -> post('meja');
		$staf	= $this -> input -> post('staf');
		$cat 	= $this -> input -> post('catatan');
		$menu 	= $this -> input -> post('menu[]');
		$qty 	= $this -> input -> post('menu_qty[]');
		$cat_det= $this -> input -> post('catatan_detail[]');
		#make sure its not null
		if(!isset($meja) or !isset($staf)):
			$data['status'] = 'failed';
			$data['msg']	= 'Data meja dan staf login tidak ditemukan';
		else:
			if(!isset($menu) or !isset($qty)):
				$data['status'] = 'failed';
				$data['msg']	= 'mohon masukkan menu pesanan';
			else:
				if(count($menu)<1):
					$data['status'] = 'failed';
					$data['msg']	= 'mohon masukkan menu pesanan';
				else:
					$data_to_input = array('id_karyawan'=> $staf,
						'id_meja'	=> $meja,
						'catatan'	=> $cat,
						'tgl_transaksi' => date('Y-m-d H:i:s')
					);
					$input_pesanan = $this -> Query -> inputDataGetLastID($data_to_input,'transaksi');
					if($input_pesanan['is_insert'] == true):
						foreach($menu as $key => $value):
							$this -> Query -> inputData(array(	'id_transaksi' 	 => $input_pesanan['id'],
								'id_menu' 	  	 => $value,
								'jumlah_beli' 	 => $qty[$key],
								'status' 	  	 => 'wait',
								'catatan_detail' => $cat_det[$key]
							),'transaksi_detail');
						endforeach;
						$data['status'] = 'ok';
						$data['msg']	= 'ok';
					else:
						$data['status'] = 'failed';
						$data['msg']	= 'gagal input error pada database : '.$input_pesanan['error'];
					endif;
				endif;
			endif;
		endif;
		echo json_encode($data);
	}

	public function pesanan_cancel()
	{
		$id_trans_det = $this -> input -> post('id_td');
		if(!isset($id_trans_det) or $id_trans_det ==''):
			$data['status'] = 'failed';
			$data['msg']	= 'id tidak ditemukan.';
		else:
			$update = $this -> Query -> updateDataDetail( array('id_transaksi_detail'=>$id_trans_det),
				array('status'=>'cancel'),'transaksi_detail');
			if($update['is_query'] == true):
				$data['status'] = 'ok';
				$data['msg']	= 'Pesanan berhasil dibatalkan.';
			else:
				$data['status'] = 'failed';
				$data['msg']	= 'Pesanan gagal dibatalkan, kesalahan pada database : '.$update['error'];
			endif;
		endif;
		echo json_encode($data);
	}

	public function pesanan_cancel_all()
	{
		$id_trans= $this -> input -> post('id_trans');
		if(!isset($id_trans) or $id_trans ==''):
			$data['status'] = 'failed';
			$data['msg']	= 'id tidak ditemukan.';
		else:
			$update = $this -> Query -> updateDataDetail( array('id_trans'=>$id_trans),
				array('status_trans'=>'cancel'),'transaksi');
			if($update['is_query'] == true):
				$data['status'] = 'ok';
				$data['msg']	= 'Pesanan berhasil dibatalkan.';
			else:
				$data['status'] = 'failed';
				$data['msg']	= 'Pesanan gagal dibatalkan, kesalahan pada database : '.$update['error'];
			endif;
		endif;
		echo json_encode($data);
	}

	public function pesanan_selesai()
	{
		$id_trans = $this -> input -> post('id_trans');
		if(!isset($id_trans) or $id_trans ==''):
			$data['status'] = 'failed';
			$data['msg']	= 'id tidak ditemukan.';
		else:
			$data_t = $this -> Query -> getData(array('id_transaksi'=>$id_trans),'transaksi_detail') -> result();
			$update_transaksi = $this -> Query -> updateData(array('id_transaksi'=>$id_trans),array('status_trans'=>'done'),'transaksi');
			foreach($data_t as $val):
				$update = $this -> Query -> updateData(array('id_transaksi_detail'=>$val->id_transaksi_detail),
					array('status'=>'done'),'transaksi_detail');
				$updt_bahan = $this -> updateBahan($val->id_menu,$val->jumlah_beli);
			endforeach;
			$data['status'] = 'ok';
			$data['msg']	= 'Pesanan berhasil diupdate.';
		endif;
		echo json_encode($data);
	}

	public function pesanan_add_menu()
	{
		$id_trans = $this -> input -> post('id_trans');
		$menu 	  = $this -> input -> post('menu[]');
		$qty 	  = $this -> input -> post('menu_qty[]');
		$cat_det= $this -> input -> post('catatan_detail[]');

		if($id_trans =='' or empty($id_trans) or !isset($id_trans)):
			$data['status'] = 'failed';
			$data['msg']	= 'id tidak ditemukan.';
		else:
			$data['transaksi'] = $this -> Query -> getData(array('id_transaksi'=>$id_trans),'transaksi')->row();
			if(count($data['transaksi'])<1):
				$data['status'] = 'failed';
				$data['msg']	= 'Transaksi tidak valid';
			else:
				foreach($menu as $key => $value):
					$this -> Query -> inputData(array(	'id_transaksi'	=> $id_trans,
						'id_menu' 	  	=> $value,
						'jumlah_beli' 	=> $qty[$key],
						'catatan_detail'=> $cat_det[$key],
						'status' 	  => 'wait'
					),'transaksi_detail');
				endforeach;
				$data['status'] = 'ok';
				$data['msg']	= 'Pesanan berhasil diupdate.';
			endif;
		endif;
		echo json_encode($data);
	}


	public function updateBahan($id_menu,$qty)
	{
		// $id_menu = $this -> input -> post('id_menu');
		// $qty 	 = $this -> input -> post('qty');
		$menu    = $this -> Query -> getData(array('id_menu'=>$id_menu),'menu') -> row();
		if(count($menu)>=1):
			$bahan = $this -> Query -> getDataBahanFromMenu(array('menu_has_bahan.id_menu'=>$id_menu)) -> result();
			if(count($bahan)>=1):
				foreach($bahan as $bhn):
					$min_stock 	 = $bhn -> stock_bahan - (($bhn -> quantity) * $qty) ;
					$data_update = array('stock_bahan'=> $min_stock);
					$updt = $this -> Query -> updateData(array('id_bahan'=>$bhn->id_bahan),$data_update,'bahan');
				endforeach;
			else:
				$data['status'] = 'failed';
				$data['msg']	= 'menu tidak ditemukan';
			endif;
		else:
		endif;

	}
	// data pesanan

	//API Tambahan
	public function login()
	{
		$u      = $this -> input -> post('username');
		$p      = $this -> input -> post('password');
		$query   = $this -> Query -> getData(array('username'=>$u,'password'=>$p),'karyawan') -> row();
		if($query):
			$data['status'] = true;
			$data['msg']	= 'berhasil login';
			$data['user']   = $query;
		else:
			$data['status'] = false;
			$data['msg']	= 'Cek kembali username atau password anda';
		endif;
		echo json_encode($data);
	}



	public function getPretransaksi()
	{
		$idMeja         = $this -> input -> post('idMeja');
		$data['data']   = $this -> Query -> getData(array('id_meja'=>$idMeja),'pre_transaksi') -> result();
		if($data):
			$data['status'] = true;
			$data['msg']	= 'ok';
		else:
			$data['status'] = false;
			$data['msg']	= 'gagal';
		endif;
		echo json_encode($data);
	}

	public function addPreeTransaktion(){

		$idMeja         = $this -> input -> post('idMeja');
		$idMenu         = $this -> input -> post('idMenu');
		$qty            = $this -> input -> post('jumbel');
		$catatan        = $this -> input -> post('catatan');
		$harga          = $this -> input -> post('harga');
		$stok           = $this -> input -> post('stok');

		$input = $this -> Query -> inputData(array(	'id_meja'     => $idMeja,
			'id_menu' 	  => $idMenu,
			'jumlah_beli' => $qty,
			'catatan' 	  => $catatan,
			'price' 	  => $harga,
		),
			'pre_transaksi');

		if($input):
			$this -> Query -> updateData(array('id_menu'=>$idMenu),
				array('stock_menu'=>$stok),'menu');
			$data['status'] = true;
			$data['msg']	= 'berhasil menambah kedaftar pesanan';
		else:
			$data['status'] = false;
			$data['msg']	= 'gagal';
		endif;
		echo json_encode($data);
	}

	public function sumCountPreTransaksi(){
		$idMeja         = $this -> input -> post('idMeja');
		$data['data']   = $this -> Query -> sum(array('id_meja'=>$idMeja)) -> row();
		if($data):
			$data['status'] = true;
			$data['msg']	= 'ok';
		else:
			$data['status'] = false;
			$data['msg']	= 'gagal';
		endif;
		echo json_encode($data);
	}


	public function detailPreTransakti()
	{
		$idMeja      = $this -> input -> post('idMeja');
		$query       = $this -> Query -> getData(array('id_meja'=>$idMeja),'v_pretransaksi') -> result();
		if($query):
			$data['status'] = true;
			$data['msg']	= 'ok';
			$data['pesanan']   = $query;
		else:
			$data['status'] = false;
			$data['msg']	= 'gagal';
		endif;
		echo json_encode($data);
	}


	public function hapusPesanan(){
		$id          = $this -> input -> post('id');
		$idMenu      = $this -> input -> post('idMenu');
		$stok        = $this -> input -> post('stok');

		$query       = $this -> Query -> delData(array('id'=>$id),'pre_transaksi');

		if($query):
			$this -> Query -> updateData(array('id_menu'=>$idMenu),
				array('stock_menu'=>$stok),'menu');
			$data['status']     = true;
			$data['msg']	    = 'Data berhasil dihapus';
		else:
			$data['status'] = false;
			$data['msg']	= 'gagal';
		endif;
		echo json_encode($data);
	}

	public function getStok(){
		$idMenu      = $this -> input -> post('idMenu');
		$query       = $this -> Query -> getData(array('id_menu'=>$idMenu),'menu') -> row();
		if($query):
			$data['status']     = true;
			$data['msg']	    = 'berhasil mengambil data';
			$data['stok']       = $query->stock_menu;
		else:
			$data['status']     = false;
			$data['msg']	    = 'Data berhasil dihapus';
		endif;
		echo json_encode($data);
	}

	public function addPesanan(){

	}
}
